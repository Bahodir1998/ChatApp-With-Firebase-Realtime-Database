package com.example.firebaserealdb

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.firebaserealdb.R
import com.example.firebaserealdb.adapters.UserRvAdapter
import com.example.firebaserealdb.databinding.FragmentChatsBinding
import com.example.firebaserealdb.models.Message
import com.example.firebaserealdb.models.OnlineUser
import com.example.firebaserealdb.models.User
import com.example.firebaserealdb.utils.AppUtil
import com.example.firebaserealdb.utils.SharedPref
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging

class ChatsFragment : Fragment() {

    lateinit var binding: FragmentChatsBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var fireBaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var referenceOnline: DatabaseReference
    lateinit var userRvAdapter: UserRvAdapter
    var list = ArrayList<User>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        fireBaseDatabase = FirebaseDatabase.getInstance()
        reference = fireBaseDatabase.getReference("users")
        referenceOnline = fireBaseDatabase.getReference("online")

        SharedPref.init(requireContext())
        val email = currentUser?.email
        val displayName = currentUser?.displayName
        val phoneNumber = currentUser?.phoneNumber
        val photoUrl = currentUser?.photoUrl
        val token = SharedPref.read(SharedPref.TOKEN,"")
        val uid = currentUser?.uid

        Log.d("TG", "onCreateView: $token")
        val user = User(email,displayName,phoneNumber,photoUrl.toString(),token,uid)
        reference.child(uid!!).setValue(user)
        val onlineUser = OnlineUser(1,uid)
        referenceOnline.child(uid).setValue(onlineUser)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                val children = snapshot.children
                for (child in children){
                    val value = child.getValue(User::class.java)
                    if (value != null&&currentUser.uid!=value.uid) {
                        list.add(value)
                    }
                }
                userRvAdapter = UserRvAdapter(list,object : UserRvAdapter.OnItemClickListener{
                    override fun onItemClick(user: User) {
                        val bundle = Bundle()
                        bundle.putSerializable("user",user)
                        findNavController().navigate(R.id.singleChatFragment,bundle)
                    }
                })
                binding.rv.adapter = userRvAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return binding.root
    }

}
package com.example.firebaserealdb

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaserealdb.adapters.CheckUserAdapter
import com.example.firebaserealdb.adapters.GroupRvAdapter
import com.example.firebaserealdb.databinding.DialogViewBinding
import com.example.firebaserealdb.databinding.FragmentGroupsBinding
import com.example.firebaserealdb.models.Group
import com.example.firebaserealdb.models.User
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupsFragment : Fragment() {

    lateinit var binding: FragmentGroupsBinding
    lateinit var groupRvAdapter: GroupRvAdapter
    lateinit var list: ArrayList<User>
    lateinit var checkedUsers: ArrayList<String>
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var checkUserAdapter: CheckUserAdapter
    lateinit var groupReferens: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupsBinding.inflate(inflater)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("users")
        groupReferens = firebaseDatabase.getReference("groups")
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list = ArrayList<User>()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(User::class.java)
                    if (value!=null&&value.uid!=firebaseAuth.currentUser?.uid){
                        list.add(value)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        binding.addGroup.setOnClickListener {
            checkedUsers = ArrayList()
            checkedUsers.add(firebaseAuth.currentUser?.uid!!)
            val dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_view)
            dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent)
            checkUserAdapter = CheckUserAdapter(list,object : CheckUserAdapter.OnItemClickListener{
                override fun onItemClick(uid: String, isChacked: Boolean) {
                    Log.d("TTT", "chackbox: $isChacked")
                    if (isChacked){
                        checkedUsers.add(uid)
                    }else{
                        checkedUsers.remove(uid)
                    }
                }

            })
            dialog.findViewById<RecyclerView>(R.id.users_rv).adapter = checkUserAdapter
            Log.d("TTT", "list: $list")
            dialog.findViewById<MaterialButton>(R.id.close).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<MaterialButton>(R.id.ok).setOnClickListener {
                val groupName = dialog.findViewById<TextInputEditText>(R.id.group_name_et).text.toString()
                if (!groupName.isNullOrBlank()){
                    val key = groupReferens.push().key
                    val group = Group(groupName,checkedUsers,key, firebaseAuth.currentUser!!.uid)
                    groupReferens.child("$key").setValue(group)
                    dialog.dismiss()
                }
            }
            dialog.setCancelable(false)
            dialog.show()
        }
        groupReferens.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupList = ArrayList<Group>()
                val children = snapshot.children
                for (child in children) {
                    val group = child.getValue(Group::class.java)
                    if (group!=null&& group.usersList?.contains(firebaseAuth.currentUser?.uid!!)!!){
                        groupList.add(group)
                    }
                }
                groupRvAdapter = GroupRvAdapter(groupList,object : GroupRvAdapter.OnItemClickListener{
                    override fun onItemClick(group: Group) {
                        val bundle = Bundle()
                        bundle.putSerializable("group",group)
                        findNavController().navigate(R.id.chatgroupFragment,bundle)
                    }
                })
                binding.groupRv.adapter = groupRvAdapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }

}
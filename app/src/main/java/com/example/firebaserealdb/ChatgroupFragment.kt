package com.example.firebaserealdb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.firebaserealdb.adapters.GroupMessageAdapter
import com.example.firebaserealdb.databinding.FragmentChatgroupBinding
import com.example.firebaserealdb.models.Group
import com.example.firebaserealdb.models.GroupMessage
import com.example.firebaserealdb.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class ChatgroupFragment : Fragment() {

    lateinit var binding: FragmentChatgroupBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var groupMessageAdapter: GroupMessageAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatgroupBinding.inflate(inflater)

        val group = arguments?.getSerializable("group") as Group
        binding.tv.setText(group.groupName)
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("messages")
        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.editText.addTextChangedListener {
            if (binding.editText.text.isNullOrBlank()){
                binding.addOrSend.setImageResource(R.drawable.ic_baseline_add_24)
            }else{
                binding.addOrSend.setImageResource(R.drawable.ic_baseline_send_24)
            }
        }
        binding.addOrSend.setOnClickListener {
            val m = binding.editText.text.toString()
            if (!m.isNullOrBlank()) {
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val date = simpleDateFormat.format(Date())
                val groupMessage = GroupMessage(m,date,currentUser?.photoUrl.toString(),0)
                val key = reference.push().key
                reference.child("${group.groupUid}/$key").setValue(groupMessage)
                binding.editText.setText("")
            }
        }
        reference.child("${group.groupUid}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<GroupMessage>()
                val children = snapshot.children
                for (child in children) {
                    val value = child.getValue(GroupMessage::class.java)
                    if (value != null) {
                        if (value.imgUrl==currentUser?.photoUrl.toString()){
                            value.isMine=1
                            list.add(value)
                        }else{
                            list.add(value)
                        }
                    }
                }

                groupMessageAdapter = GroupMessageAdapter(list)
                binding.messageRv.adapter = groupMessageAdapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        return binding.root
    }

}
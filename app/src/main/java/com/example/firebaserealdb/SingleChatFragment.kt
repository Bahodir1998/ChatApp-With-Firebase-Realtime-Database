package com.example.firebaserealdb

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.firebaserealdb.adapters.MessageAdapter
import com.example.firebaserealdb.databinding.FragmentSingleChatBinding
import com.example.firebaserealdb.models.Message
import com.example.firebaserealdb.models.OnlineUser
import com.example.firebaserealdb.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SingleChatFragment : Fragment() {

    lateinit var binding: FragmentSingleChatBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var messageAdapter: MessageAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleChatBinding.inflate(inflater)

        val user = arguments?.getSerializable("user") as User
        Picasso.get().load(user.photoUrl).into(binding.imgView)
        binding.nameTv.setText(user.displayName)

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        firebaseDatabase = FirebaseDatabase.getInstance()
        reference = firebaseDatabase.getReference("messages")

        firebaseDatabase.getReference("online/${user.uid}")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("ResourceAsColor")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val value = snapshot.getValue(OnlineUser::class.java)
                    if (value != null && value.isOnline == 1) {
                        binding.online.setText("Online")
                    } else {
                        binding.online.setText("Offline")
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        binding.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (binding.editText.text.isNullOrBlank()) {
                    binding.addOrSend.setImageResource(R.drawable.ic_baseline_add_24)
                } else {
                    binding.addOrSend.setImageResource(R.drawable.ic_baseline_send_24)
                }
            }

        })

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.addOrSend.setOnClickListener {
            val m = binding.editText.text.toString()
            if (!m.isNullOrBlank()) {
                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val date = simpleDateFormat.format(Date())
                val message = Message(m, date, 1)
                val key = reference.push().key
                reference.child("${currentUser?.uid}/${user.uid}/$key").setValue(message)
                message.isMine = 0
                reference.child("${user.uid}/${currentUser?.uid}/$key").setValue(message)
                binding.editText.setText("")
                firebaseDatabase.getReference("lastmessage").child("${currentUser?.uid}/${user.uid}").setValue(message)
                firebaseDatabase.getReference("lastmessage").child("${user.uid}/${currentUser?.uid}")
                    .setValue(message)
            }
        }

        reference.child("${currentUser?.uid}/${user.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = ArrayList<Message>()
                    val children = snapshot.children
                    for (child in children) {
                        val value = child.getValue(Message::class.java)
                        if (value != null) {
                            list.add(value)
                        }
                    }

                    messageAdapter = MessageAdapter(list)
                    binding.messageRv.adapter = messageAdapter
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        return binding.root
    }

}
package com.example.firebaserealdb.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaserealdb.databinding.UserItemBinding
import com.example.firebaserealdb.models.Message
import com.example.firebaserealdb.models.OnlineUser
import com.example.firebaserealdb.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class UserRvAdapter(var list:List<User>,var onItemClickListener: OnItemClickListener):RecyclerView.Adapter<UserRvAdapter.Vh>() {

    lateinit var fireBaseDatabase: FirebaseDatabase
    lateinit var reference: DatabaseReference
    lateinit var referenceOnline: DatabaseReference
    lateinit var firebaseAuth: FirebaseAuth
    inner class Vh(var userItemBinding: UserItemBinding):RecyclerView.ViewHolder(userItemBinding.root){

        fun onBind(user: User){
            firebaseAuth = FirebaseAuth.getInstance()
            fireBaseDatabase = FirebaseDatabase.getInstance()
            reference = fireBaseDatabase.getReference("lastmessage")
            referenceOnline = fireBaseDatabase.getReference("online")
            referenceOnline.child("${user.uid}").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(OnlineUser::class.java)
                    if (value!=null&&value.isOnline==1){
                        userItemBinding.rectangle.visibility = View.VISIBLE
                        userItemBinding.circle.visibility = View.VISIBLE
                    }else{
                        userItemBinding.rectangle.visibility = View.INVISIBLE
                        userItemBinding.circle.visibility = View.INVISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            reference.child("${firebaseAuth.currentUser?.uid}/${user.uid}").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val message = snapshot.getValue(Message::class.java)
                    if (message!=null && message?.message?.length!! >20){
                        userItemBinding.lastMessage.setText(message?.message!!.substring(0,20)+"...")
                    }else{
                        userItemBinding.lastMessage.setText(message?.message)
                    }
                    userItemBinding.timeTv.setText(message?.date?.substring(11))
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
            if (user.displayName?.length!! <=20){
                userItemBinding.nameTv.setText(user.displayName)
            }else{
                userItemBinding.nameTv.setText(user.displayName?.substring(17)+"...")
            }

            Picasso.get().load(user.photoUrl).into(userItemBinding.imgView)

            userItemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(UserItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener{
        fun onItemClick(user: User)
    }
}
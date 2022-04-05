package com.example.firebaserealdb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaserealdb.databinding.FromItemBinding
import com.example.firebaserealdb.databinding.GroupUserItemBinding
import com.example.firebaserealdb.models.GroupMessage
import com.squareup.picasso.Picasso

class GroupMessageAdapter(var list: List<GroupMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromVh(var fromItemBinding: FromItemBinding) :
        RecyclerView.ViewHolder(fromItemBinding.root) {
        fun onBind(groupMessage: GroupMessage) {
            fromItemBinding.messageTv.setText(groupMessage.message)
            fromItemBinding.date.setText(groupMessage.date?.substring(11))
        }
    }

    inner class ToVh(var groupUserItemBinding: GroupUserItemBinding) :
        RecyclerView.ViewHolder(groupUserItemBinding.root) {
        fun onBind(groupMessage: GroupMessage) {
            Picasso.get().load(groupMessage.imgUrl).into(groupUserItemBinding.avatarImg)
            groupUserItemBinding.messageTv.setText(groupMessage.message)
            groupUserItemBinding.date.setText(groupMessage.date?.substring(11))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            return FromVh(
                FromItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ToVh(GroupUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position].isMine == 1) {
            return 1
        } else {
            return 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1) {
            val fromVh = holder as FromVh
            fromVh.onBind(list[position])
        } else {
            val toVh = holder as ToVh
            toVh.onBind(list[position])
        }
    }

    override fun getItemCount(): Int = list.size


}
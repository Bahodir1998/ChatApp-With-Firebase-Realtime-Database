package com.example.firebaserealdb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaserealdb.databinding.GroupItemBinding
import com.example.firebaserealdb.models.Group

class GroupRvAdapter(var list: List<Group>, var onItemClickListener: OnItemClickListener):RecyclerView.Adapter<GroupRvAdapter.Vh>() {

    inner class Vh(var groupItemBinding: GroupItemBinding):RecyclerView.ViewHolder(groupItemBinding.root){
        fun onBind(group: Group){
            groupItemBinding.tv.setText(group.groupName)

            groupItemBinding.root.setOnClickListener {
                onItemClickListener.onItemClick(group)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(GroupItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener{
        fun onItemClick(group: Group)
    }
}
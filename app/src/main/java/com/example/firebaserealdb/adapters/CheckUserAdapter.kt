package com.example.firebaserealdb.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaserealdb.databinding.CheckUserItemBinding
import com.example.firebaserealdb.models.User

class CheckUserAdapter(var list: List<User>, var onItemClickListener: OnItemClickListener):RecyclerView.Adapter<CheckUserAdapter.Vh>() {

    inner class Vh(var checkUserItemBinding: CheckUserItemBinding):RecyclerView.ViewHolder(checkUserItemBinding.root){
        fun onBind(user: User){

            checkUserItemBinding.checkbox.text = user.displayName
            checkUserItemBinding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                onItemClickListener.onItemClick(user.uid!!,isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(CheckUserItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    interface OnItemClickListener{
        fun onItemClick(uid: String,isChacked: Boolean)
    }
}
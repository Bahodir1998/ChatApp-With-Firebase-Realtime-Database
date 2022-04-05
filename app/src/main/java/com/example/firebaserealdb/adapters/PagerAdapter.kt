package com.example.firebaserealdb.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.firebaserealdb.ChatsFragment
import com.example.firebaserealdb.GroupsFragment

class PagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position==0){
            return ChatsFragment()
        }else{
            return GroupsFragment()
        }
    }
}
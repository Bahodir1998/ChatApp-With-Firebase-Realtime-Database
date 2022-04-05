package com.example.firebaserealdb

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.firebaserealdb.adapters.PagerAdapter
import com.example.firebaserealdb.databinding.FragmentViewPagerBinding
import com.example.firebaserealdb.databinding.TabItemBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPagerFragment : Fragment() {

    lateinit var binding: FragmentViewPagerBinding
    lateinit var pagerAdapter: PagerAdapter
    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentViewPagerBinding.inflate(inflater)

        pagerAdapter = PagerAdapter(requireActivity())
        binding.viewPager.adapter = pagerAdapter

        val tabList = listOf("Chats","Groups")
        TabLayoutMediator(binding.tabLayout,binding.viewPager){tab, position ->
            val tabItemBinding = TabItemBinding.inflate(LayoutInflater.from(requireContext()),null,false)
            tabItemBinding.tv.setText(tabList[position])
            tab.customView = tabItemBinding.root

            if (position == 0){
                val customView = tab.customView
                val bind1 = TabItemBinding.bind(customView!!)
                bind1.tv.setTextColor(Color.parseColor("#ffffff"))
                bind1.card.setCardBackgroundColor(Color.parseColor("#2675EC"))
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val customView = tab?.customView
                val bind1 = TabItemBinding.bind(customView!!)
                bind1.tv.setTextColor(Color.parseColor("#ffffff"))
                bind1.card.setCardBackgroundColor(Color.parseColor("#2675EC"))
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val customView = tab?.customView
                val bind1 = TabItemBinding.bind(customView!!)
                bind1.tv.setTextColor(Color.parseColor("#848484"))
                bind1.card.setCardBackgroundColor(Color.parseColor("#E5E5E5"))
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
        return binding.root
    }

}
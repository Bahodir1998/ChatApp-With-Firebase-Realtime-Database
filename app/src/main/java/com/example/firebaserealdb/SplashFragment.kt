package com.example.firebaserealdb

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.firebaserealdb.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater)

        val handler = Handler()

        handler.postDelayed(runnable, 2000)

        return binding.root
    }

    var runnable = object : Runnable {
        override fun run() {
            auth = FirebaseAuth.getInstance()
            if (auth.currentUser!=null){
                val controller = Navigation.findNavController(binding.root)
                controller.popBackStack(R.id.splashFragment, true)
                controller.navigate(R.id.viewPagerFragment)
            }else{
                val controller = Navigation.findNavController(binding.root)
                controller.popBackStack(R.id.splashFragment, true)
                controller.navigate(R.id.signInFragment)
//            findNavController().navigate(R.id.firthFragment)
            }
        }

    }
}
package com.example.firebaserealdb

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.firebaserealdb.adapters.PrefsHelper
import com.example.firebaserealdb.databinding.FragmentSplashBinding
import com.example.firebaserealdb.utils.AppUtil
import com.example.firebaserealdb.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var auth: FirebaseAuth
    lateinit var token: String
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
                FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->

                    Log.d("TTT","TOKEN = $result")
                    if(result != null){
                        token = result
                        SharedPref.init(binding.root.context)
                        SharedPref.write(SharedPref.TOKEN,token)
                        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                            .child(AppUtil().getUID())
                        val map:MutableMap<String,Any> = HashMap()
                        map["token"] = token
                        databaseReference.updateChildren(map)
                    }
                }
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
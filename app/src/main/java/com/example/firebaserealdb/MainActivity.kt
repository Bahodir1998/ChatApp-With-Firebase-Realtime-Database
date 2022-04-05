package com.example.firebaserealdb

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.firebaserealdb.databinding.ActivityMainBinding
import com.example.firebaserealdb.models.OnlineUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    private  val TAG = "TTT"
    lateinit var binding: ActivityMainBinding
    lateinit var controller: NavController
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var fireBaseDatabase: FirebaseDatabase
    lateinit var referenceOnline: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onDestroy() {
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        fireBaseDatabase = FirebaseDatabase.getInstance()
        referenceOnline = fireBaseDatabase.getReference("online")
        val onlineUser = OnlineUser(0,currentUser?.uid)
        referenceOnline.child(currentUser?.uid!!).setValue(onlineUser)
        super.onDestroy()
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(com.example.firebaserealdb.R.id.my_nav_host_fragment)
        val backStackEntryCount = navHostFragment?.childFragmentManager?.backStackEntryCount

        Log.d(TAG, "backStackCount: $backStackEntryCount")
        if (backStackEntryCount == 0) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Tanlang!")
            builder.setMessage("dasturni yopish uchun \"Ok\" bosing")
            builder.setPositiveButton("Ok"){dialogInterface, which ->
                finish()
            }
            builder.setNegativeButton("Cancel"){dialogInterface, which ->

            }
            builder.setNeutralButton("LogOut"){dialogInterface, which ->
                firebaseAuth = FirebaseAuth.getInstance()
                val currentUser = firebaseAuth.currentUser

                fireBaseDatabase = FirebaseDatabase.getInstance()
                referenceOnline = fireBaseDatabase.getReference("online")
                val onlineUser = OnlineUser(0,currentUser?.uid)
                referenceOnline.child(currentUser?.uid!!).setValue(onlineUser)
                firebaseAuth.signOut()
                controller = Navigation.findNavController(this@MainActivity,com.example.firebaserealdb.R.id.my_nav_host_fragment)
                controller.popBackStack(com.example.firebaserealdb.R.id.viewPagerFragment, true)
                controller.navigate(com.example.firebaserealdb.R.id.signInFragment)

            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }else{
            super.onBackPressed()
        }

    }


}
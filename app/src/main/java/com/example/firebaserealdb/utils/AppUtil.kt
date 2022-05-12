package com.example.firebaserealdb.utils

import com.google.firebase.auth.FirebaseAuth

class AppUtil {

    lateinit var firebaseAuth:FirebaseAuth
    public fun getUID():String{
        firebaseAuth = FirebaseAuth.getInstance()
        val uid = firebaseAuth.currentUser?.uid
        return uid!!
    }
}
package com.example.firebaserealdb.models

import java.io.Serializable

class User:Serializable {
    var email:String? = null
    var displayName: String? = null
    var phoneNumber:String? = null
    var photoUrl:String? = null
    var token:String? = null
    var uid:String? = null




    constructor()
    constructor(
        email: String?,
        displayName: String?,
        phoneNumber: String?,
        photoUrl: String?,
        token: String?,
        uid: String?
    ) {
        this.email = email
        this.displayName = displayName
        this.phoneNumber = phoneNumber
        this.photoUrl = photoUrl
        this.token = token
        this.uid = uid
    }
}
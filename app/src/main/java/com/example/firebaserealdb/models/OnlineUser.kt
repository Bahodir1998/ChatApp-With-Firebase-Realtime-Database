package com.example.firebaserealdb.models

import java.io.Serializable

class OnlineUser : Serializable {
    var isOnline: Int? = null
    var uid: String? = null

    constructor()

    constructor(isOnline: Int?, uid: String?) {
        this.isOnline = isOnline
        this.uid = uid
    }

}
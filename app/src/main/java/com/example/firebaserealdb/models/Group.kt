package com.example.firebaserealdb.models

import java.io.Serializable

class Group:Serializable {
    var groupName: String? = null
    var usersList: ArrayList<String>? = null
    var groupUid: String? = null
    var owner: String? = null

    constructor()
    constructor(
        groupName: String?,
        usersList: ArrayList<String>?,
        groupUid: String?,
        owner: String?
    ) {
        this.groupName = groupName
        this.usersList = usersList
        this.groupUid = groupUid
        this.owner = owner
    }

}
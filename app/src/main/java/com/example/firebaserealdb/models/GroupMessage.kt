package com.example.firebaserealdb.models

class GroupMessage {

    var message: String? = null
    var date: String? = null
    var imgUrl: String? = null
    var isMine: Int? = null

    constructor()
    constructor(message: String?, date: String?, imgUrl: String?, isMine: Int?) {
        this.message = message
        this.date = date
        this.imgUrl = imgUrl
        this.isMine = isMine
    }
}
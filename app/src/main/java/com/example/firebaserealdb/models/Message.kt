package com.example.firebaserealdb.models

class Message {
    var message: String? = null
    var date: String? = null
    var isMine: Int? = null

    constructor()

    constructor(message: String?, date: String?, isMine: Int?) {
        this.message = message
        this.date = date
        this.isMine = isMine
    }
}
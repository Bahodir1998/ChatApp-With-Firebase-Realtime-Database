package com.example.firebaserealdb.Notifications

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {

    @POST("fcm/send")
    fun sendNotification(
        @Body body:Sender,
        @Header("Content-Type") contentType:String,
        @Header("Authorization") key:String,
    ):Call<MyResponce>
}
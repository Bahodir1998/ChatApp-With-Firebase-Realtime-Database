package com.example.firebaserealdb.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide.with
import com.example.firebaserealdb.R
import com.example.firebaserealdb.SingleChatFragment
import com.example.firebaserealdb.constants.AppConstants
import com.example.firebaserealdb.utils.AppUtil
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.HashMap

class FirebaseNotificationService : FirebaseMessagingService() {

    private val appUtil = AppUtil()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()){
            val map: Map<String,String> = remoteMessage.data

            Log.d("MAP", "onMessageReceived: $map")
            val title = map["title"]
            val message = map["body"]
            val hisId = map["user"]
            val hisImage = map["icon"]
            val hisUid = map["sented"]

            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                createOreoNotification(title!!,message!!,hisId!!,hisImage!!,hisUid!!)
            }else{
                createNormalNotification(title!!,message!!,hisId!!,hisImage!!,hisUid!!)
            }
        }
    }

    private fun updateToken(token: String) {

        val databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(appUtil.getUID())
        val map: MutableMap<String, Any> = HashMap()
        map["token"] = token
        databaseReference.updateChildren(map)
    }

    private fun createNormalNotification(
        title: String,
        message: String,
        hisId: String,
        hisImage: String,
        hisUid: String
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, AppConstants.CANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.primary, null))
            .setSound(uri)

        val inteent = Intent(this, SingleChatFragment::class.java)
        inteent.putExtra("hisId", hisId)
        inteent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, inteent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())
    }

    @SuppressLint("ServiceCast")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        hisId: String,
        hisImage: String,
        hisUid: String
    ) {

        val chanel = NotificationChannel(AppConstants.CANNEL_ID,"Message",NotificationManager.IMPORTANCE_HIGH)

        chanel.setShowBadge(true)
        chanel.enableLights(true)
        chanel.enableVibration(true)
        chanel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chanel)

        val inteent = Intent(this, SingleChatFragment::class.java)
        inteent.putExtra("hisId", hisId)
        inteent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, inteent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this,AppConstants.CANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.chatapp)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.primary, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100,notification)
    }
}
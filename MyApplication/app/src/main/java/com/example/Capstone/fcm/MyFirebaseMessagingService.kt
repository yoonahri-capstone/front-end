package com.example.Capstone.fcm

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.Capstone.ImageUtils.getCircularBitmap
import com.example.Capstone.R
import com.example.Capstone.activities.InformationActivity
import com.example.Capstone.background.AlarmBroadcastReceiver
import com.example.Capstone.db.SharedPreferenceController
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    val NOTICATION_ID = 227
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("fcmmessage", "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        remoteMessage.data.isNotEmpty().let {
            Log.d("fcmmessage", "Message data payload: " + remoteMessage.data)

//            if (/* Check if data needs to be processed by long running job */ true) {
//                // For long-running tasks (10 seconds or more) use WorkManager.
//                scheduleJob()
//            } else {
//                // Handle message within 10 seconds
//                handleNow()
//            }
        }

        if (SharedPreferenceController.getUserNoti(this)!!){
            // Check if message contains a notification payload.
            when (remoteMessage.data["type"])
            {
                "upload" -> sendUploadNotification(remoteMessage.data["sharing"]!!, remoteMessage.data["image"]!!, remoteMessage.data["scrap_id"]!!.toInt())
                "invite" -> sendInviteNotification(remoteMessage.data["sharing"]!!)
                "delete" -> sendDeleteNotification(JSONArray(remoteMessage.data["scraps"]))
            }
        }
    }

        // Check if message contains a notification payload.
//        remoteMessage.notification?.let {
//            Log.d(TAG, "Message Notification Body: ${it.body}")
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(token, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(token, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendUploadNotification(messageBody: String, url: String, scrap_id: Int) {

        val intent = Intent(this, InformationActivity::class.java)
        intent.putExtra("id", scrap_id)
        intent.putExtra("noti", true)

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,  //default값 0
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        var builder = NotificationCompat.Builder(this, "멤멤")
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle("새 글 알림")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        Glide.with(this)
                            .asBitmap()
                            .load(url)
                            .into(object : SimpleTarget<Bitmap?>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                                    val bitmap = getCircularBitmap(resource!!)
                                    builder.setContentText(messageBody + "에 새 글이 공유되었습니다!")
                                    builder.setLargeIcon(bitmap)
                                    NotificationManagerCompat.from(applicationContext).notify(NOTICATION_ID, builder.build())
                                }
                            })

//        NotificationManagerCompat.from(this).notify(NOTICATION_ID, builder.build())
    }

    private fun sendInviteNotification(messageBody: String) {

        Log.d("jsonobject", messageBody)
        val permitIntent = Intent(this, AlarmBroadcastReceiver::class.java)
        permitIntent.putExtra("id", 151)
        permitIntent.putExtra("sharing_name", messageBody)

        val rejectIntent = Intent(this, AlarmBroadcastReceiver::class.java)
        rejectIntent.putExtra("id", 444)

        val acceptPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 0, permitIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val rejectPendingIntent: PendingIntent = PendingIntent.getBroadcast(this, 1110, rejectIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(this, "멤멤")
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle("초대 알림")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentText(messageBody + "에 초대되었습니다")
            .addAction(R.drawable.ic_launcher_foreground, "수락", acceptPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "거절", rejectPendingIntent)

        NotificationManagerCompat.from(this).notify(NOTICATION_ID, builder.build())

    }

    private fun sendDeleteNotification(messageBody: JSONArray) {

        var titleList = ""

        Log.d("jsonarray", messageBody.toString())

        for (i in 0 until messageBody.length()) {
            val jsonTitle = messageBody.getJSONObject(i)
            val title = jsonTitle.getString("title")
            Log.d("jsonarraytitle", title)
            titleList = "$title, $titleList"
        }

        val builder = NotificationCompat.Builder(this, "멤멤")
            .setSmallIcon(R.drawable.ic_noti)
            .setContentTitle("삭제 알림")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentText("다음 글이 삭제되었습니다")
            .setStyle(NotificationCompat.BigTextStyle().bigText(titleList))

        NotificationManagerCompat.from(this).notify(NOTICATION_ID, builder.build())
    }
}
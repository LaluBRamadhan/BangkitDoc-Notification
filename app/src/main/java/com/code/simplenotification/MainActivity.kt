package com.code.simplenotification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.code.simplenotification.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted: Boolean ->
        if(isGranted){
            Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Notification Permission Rejected", Toast.LENGTH_SHORT).show()
        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val title = getString(R.string.notification_title)
        val message = getString(R.string.notification_message)


        binding.btnSendNotification.setOnClickListener{
            sendNotification(title,message)
        }


        binding.btnOpenDetail.setOnClickListener{
            val detailIntent = Intent(this@MainActivity, DetailActivity::class.java)
            detailIntent.putExtra(DetailActivity.EXTRA_TITLE, title)
            detailIntent.putExtra(DetailActivity.EXTRA_MESSAGE, message)
            startActivity(detailIntent)
        }
        if (Build.VERSION.SDK_INT >= 33){
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    fun sendNotification(title:String, message:String){

        val notifDetailIntent = Intent(this, DetailActivity::class.java)
        notifDetailIntent.putExtra(DetailActivity.EXTRA_TITLE,title)
        notifDetailIntent.putExtra(DetailActivity.EXTRA_MESSAGE,message)

        val pendingIntent = TaskStackBuilder.create(this).run{
            addNextIntentWithParentStack(notifDetailIntent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }else{
                getPendingIntent(NOTIFICATION_ID,PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.notification)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.notifications_white))
            .setContentTitle(resources.getString(R.string.notification_title))
            .setContentText(resources.getString(R.string.notification_message))
            .setSubText(getString(R.string.notification_subtext))
            .setAutoCancel(true)

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    companion object{
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
    }


}
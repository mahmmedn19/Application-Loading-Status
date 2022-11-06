package com.example.loadingbutton


import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.content_detail.view.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    private var radioGroup: RadioGroup? = null
    private lateinit var radioButton: RadioButton
    lateinit var fileName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        radioGroup = findViewById(R.id.radioGroup)


        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
            custom_button.setOnClickListener {
            val intSelectButton: Int = radioGroup!!.checkedRadioButtonId
            if (radioGroup!!.checkedRadioButtonId == -1) {
                Toast.makeText(
                    applicationContext,
                    "Choose a download",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                custom_button.setState(ButtonState.Loading)
                radioButton = findViewById(intSelectButton)
                onRadioButtonClicked(radioButton)
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.apply {
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = getString(R.string.notification_description)
                notificationManager = getSystemService(
                    NotificationManager::class.java
                )
                notificationManager.createNotificationChannel(notificationChannel)

            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            context?.let {
                notificationManager.sendNotification(
                    getString(R.string.notification_description),
                    it, checkStatus(id!!), fileName
                )
            }
            custom_button.setState(ButtonState.Completed)
        }
    }


    private fun onRadioButtonClicked(view: View) {
        when (view.id) {
            R.id.radioBtnGlide -> {
                download(URL_glide)
                fileName = getString(R.string.glide)
            }
            R.id.radioBtnLoadImage -> {
                download(URL)
                fileName = getString(R.string.loadapp)
            }
            R.id.radioBtnRetrofit -> {
                download(URL_retrofit)
                fileName = getString(R.string.retrofit)
            }
        }
    }

    private fun download(url: String) {
        val request =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            } else {
                TODO("VERSION.SDK_INT < N")
            }


        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_glide = "https://codeload.github.com/bumptech/glide/zip/master"
        private const val URL_retrofit = "https://codeload.github.com/square/retrofit/zip/master"
    }

    private fun checkStatus(downloadReference: Long): String {
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val myDownloadQuery = DownloadManager.Query()
        myDownloadQuery.setFilterById(downloadReference)
        val cursor: Cursor = downloadManager.query(myDownloadQuery)

        if (cursor.moveToFirst()) {

            val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(columnIndex)
            var statusText = ""
            when (status) {
                DownloadManager.STATUS_FAILED -> statusText = getString(R.string.failed)
                DownloadManager.STATUS_PAUSED -> statusText = getString(R.string.paused)
                DownloadManager.STATUS_PENDING -> statusText = getString(R.string.pending)
                DownloadManager.STATUS_RUNNING -> statusText = getString(R.string.running)
                DownloadManager.STATUS_SUCCESSFUL -> statusText = getString(R.string.successful)
            }
            return statusText
        }
        return "No Status Found"
    }
}

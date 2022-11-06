package com.example.loadingbutton

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

class DetailActivity : AppCompatActivity() {

    private lateinit var toolbar :Toolbar
    private lateinit var textViewFileName: TextView
    private lateinit var textViewStatus: TextView
    private lateinit var btnFinish: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        toolbar = findViewById(R.id.toolbar_details)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()
        textViewFileName = findViewById(R.id.fileNameValue)
        textViewStatus = findViewById(R.id.statusValue)
        btnFinish = findViewById(R.id.btnFinish)
        val txtExtras = intent.extras
        if (txtExtras!=null){
            textViewFileName.text = txtExtras.getString("EXTRA_FILENAME")!!
            textViewStatus.text = txtExtras.getString("EXTRA_DOWNLOAD_STATUS")!!
            if (textViewStatus.text == getString(R.string.successful))
                textViewStatus.setTextColor(ContextCompat.getColor(applicationContext, R.color.teal_700)
                )
            else
                textViewStatus.setTextColor(ContextCompat.getColor(applicationContext, androidx.appcompat.R.color.error_color_material_dark))
        } else {
            textViewStatus.text = getString(R.string.no_found_text)
            textViewFileName.text = getString(R.string.no_found_text)
        }
        btnFinish.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

}

package com.example.s3fileloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            download()
        }
    }

    private fun download() {
        try {
            val link = "https://public-ensekitt.s3.ap-northeast-1.amazonaws.com/DarkBase.png"
            val fileName = link.substring(link.lastIndexOf("/") + 1)

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(link))

            request.setTitle(fileName)
            request.setDescription("Downloading")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)

            val downloadId = manager.enqueue(request)

            val receiver = object : BroadcastReceiver() {
                //ダウンロード完了後の処理
                override fun onReceive(context: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        //intentによりファイルを開く
                        val openFileIntent = Intent(Intent.ACTION_VIEW)
                        val uri = manager.getUriForDownloadedFile(id)
                        openFileIntent.setDataAndType(uri, contentResolver.getType(uri))
                        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(openFileIntent)
                    }
                }
            }
            registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        } catch (e: Exception) {
            Log.e(localClassName, "Cancel", e)
        }
    }
}
package com.bong.autotranscriber

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.midisheetmusic.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPerms()
    }

    private fun requestPerms(): Boolean {
        if (
            (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(this.applicationContext, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED)

        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
                ),
                0
            )
        }
        return true;
    }
}

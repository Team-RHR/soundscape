package com.example.kkgroup.soundscape_v2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kkgroup.soundscape_v2.activity.AudioFilesActivity
import com.example.kkgroup.soundscape_v2.activity.LoginActivity
import com.example.kkgroup.soundscape_v2.activity.RecordingActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        startRecordingActivityBtn.setOnClickListener {
            val intent = Intent(this, RecordingActivity::class.java)
            startActivity(intent)
        }
        redirectButton.setOnClickListener {
            val intent = Intent (this, AudioFilesActivity::class.java)
            startActivity(intent)
        }

        submitLoginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}



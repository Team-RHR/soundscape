package com.example.kkgroup.soundscape_v2

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // use file from "raw"- folder for testing purposes
        var audioFile = R.raw.muscle_car

        val playIntent = Intent(this, PlayActivity::class.java)
        playIntent.putExtra("audio", audioFile)

        startActivity(playIntent)
    }

}

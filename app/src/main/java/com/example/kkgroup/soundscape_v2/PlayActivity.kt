package com.example.kkgroup.soundscape_v2

import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_play.*

class PlayActivity : AppCompatActivity() {

    private lateinit var mPlayer: MediaPlayer
    var currentPos: Int = 0
    val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        val audioFile = intent.extras.getString("audio")
        mPlayer = MediaPlayer.create(this, Uri.parse(audioFile))

        audioDurationText.text = getTimeString(currentPos.toLong())

        // play/pause button
        mainButton.setOnClickListener {
            playPause()
        }

        // go backwards 5 seconds
        backwardBtn.setOnClickListener {
            if (mPlayer.isPlaying) {
                mPlayer.pause()
                currentPos = mPlayer.currentPosition
                mPlayer.seekTo(currentPos - 5000)
                mPlayer.start()
            }
        }

        // go forward 5 seconds
        forwardBtn.setOnClickListener {
            if (mPlayer.isPlaying) {
                mPlayer.pause()
                currentPos = mPlayer.currentPosition
                mPlayer.seekTo(currentPos + 5000)
                mPlayer.start()
            }
        }

        // listener for when audio file playback is completed
        mPlayer.setOnCompletionListener {
            currentPos = 0
            mainButton.setImageResource(R.drawable.ic_play_arrow)
            audioDurationText.text = getTimeString(currentPos.toLong())

            handler.removeCallbacks(runnable)
        }
    }

    override fun onStop() {
        super.onStop()

        handler.removeCallbacks(runnable)
    }

    private fun playPause() {
        if (mPlayer.isPlaying) {
            currentPos = mPlayer.currentPosition
            mPlayer.pause()
            mainButton.setImageResource(R.drawable.ic_play_arrow)
        } else {
            mPlayer.seekTo(currentPos)
            mPlayer.start()
            mainButton.setImageResource(R.drawable.ic_stop_black)
        }

        runnable.run()
    }

    // this runnable will update the ui to show elapsed time
    private val runnable = object : Runnable {

        val delay: Long = 500 //milliseconds (0.5 seconds)

        override fun run() {
            audioDurationText.text = getTimeString(mPlayer.currentPosition.toLong())

            // debug logs
            Log.d("DEBUG", "Current position: "+getTimeString(mPlayer.currentPosition.toLong()))
            Log.d("DEBUG", "currentPos: "+currentPos)

            handler.postDelayed(this, delay)
        }
    }

    private fun getTimeString(millis: Long): String {
        val buf = StringBuffer()

        val hours = (millis / (1000 * 60 * 60)).toInt()
        val minutes = (millis % (1000 * 60 * 60) / (1000 * 60)).toInt()
        val seconds = (millis % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        buf
                .append(String.format("%02d", hours))
                .append(":")
                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds))

        return buf.toString()
    }


}

package com.example.kkgroup.soundscape_v2.activity

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import kotlinx.android.synthetic.main.activity_recording.*
import java.io.File
import java.io.IOException
import java.util.*

private const val LOG_TAG = "hero"
private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class RecordingActivity : AppCompatActivity() {

    private var permissionToRecordAccepted = false
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var audioFile:File? = null
    private var mStartPlaying = true
    private var mStartRecording = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS), REQUEST_RECORD_AUDIO_PERMISSION)
        }

        recordingBtn.setOnClickListener {
            onRecord(mStartRecording)
            if (mStartRecording) {
                recordingBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_stop))
            } else {
                recordingBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_mic))
            }

            mStartRecording = !mStartRecording
        }

        playBtn.setOnClickListener {
            onPlay(mStartPlaying)

            if (mStartPlaying) {
                playBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_stop))
            } else {
                playBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
            }

            mStartPlaying = !mStartPlaying
        }
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
        storageTV.text = audioFile?.absolutePath
    }

    private fun onPlay(start: Boolean) = if (start) {
        startPlaying()
    } else {
        stopPlaying()
    }

    private fun startPlaying() {
        mPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFile?.absolutePath)
                prepare()
                start()
                this.setOnCompletionListener {
                    playBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_play))
                    mStartPlaying = !mStartPlaying
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
    }

    private fun stopPlaying() {
        mPlayer?.release()
        mPlayer = null
    }

    private fun startRecording() {
        audioFile = File(Tools.getSoundScapePath() + "${Date().time}.3gp")

        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setAudioEncodingBitRate(16)
            setAudioSamplingRate(44100)
            setOutputFile(audioFile?.absolutePath)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        mRecorder?.apply {
            stop()
            release()
        }
        mRecorder = null
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    override fun onStop() {
        super.onStop()
        mRecorder?.release()
        mRecorder = null
        mPlayer?.release()
        mPlayer = null
    }
}

package com.example.kkgroup.soundscape_v2

import android.Manifest
import android.media.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_recording.*
import org.jetbrains.anko.toast
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class RecordingActivity : AppCompatActivity() {

    private var isStart:Boolean = false
    private var isRecording:Boolean = false
    private val frequency = 44100
    private var filePath:String = ""
    private val channelConfiguration = AudioFormat.CHANNEL_IN_MONO
    private val audioEncoding = AudioFormat.ENCODING_PCM_16BIT
    private lateinit var file: File
    private val wavfile = File(Environment.getExternalStorageDirectory().absolutePath + "/wavtest.wav")
    private val bufferSize:Int = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recording)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS), 5)
        }

        recordingBtn.setOnClickListener { startRecording() }
        playBtn.setOnClickListener { playRecording() }
    }

    private fun startRecording() {
        if(isStart){
            recordingBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_mic))
            isRecording = !isRecording
            storageTV.text = filePath
        }else{
            recordingBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_stop))
            Thread(){
                kotlin.run {
                    doRecord()
                }
            }.start()
        }
        isStart = !isStart
    }

    private fun doRecord() {
        // Delete any previousrecording.

        file = File(Environment.getExternalStorageDirectory().absolutePath
                + File.separator + "soundscape" + "/${Date().time}.pcm")

        if (file.exists())
            file.delete()
        // Create the new file.
        try {
            file.createNewFile()
        } catch (e: IOException) {
            throw IllegalStateException("Failed to create " + file.toString())
        }
        filePath = file.absolutePath
        try {
            // Create a DataOuputStream to write the audiodata into the saved file.
            val os = FileOutputStream(file)
            val bos = BufferedOutputStream(os)
            val dos = DataOutputStream(bos)
            val audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC,
                    frequency, channelConfiguration,
                    audioEncoding, bufferSize)

            val buffer = ShortArray(bufferSize)
            // start recording
            audioRecord.startRecording()

            isRecording = true
            while (isRecording) {
                val bufferReadResult = audioRecord.read(buffer, 0, bufferSize)
                for (i in 0 until bufferReadResult)
                    dos.writeShort(buffer[i].toInt())
            }

            audioRecord.stop()
            audioRecord.release()
            dos.close()

        } catch (t: Throwable) {
            Log.e("AudioRecord", "Recording Failed")
        }
    }

    private fun playRecording() {
        if (!isStart) {
            Thread(){
                kotlin.run {
                    // Get the file we want toplayback.

                    Log.e("hero","${file.absolutePath}")
                    val file = file

                    // Get the length of the audio stored in the file(16 bit so 2 bytes per short)
                    // and create a short array to store the recordedaudio.
                    val musicLength = (file.length() / 2).toInt()
                    val music = ShortArray(musicLength)

                    try {
                        // Create a DataInputStream to read the audio databack from the saved file.
                        val fis = FileInputStream(file)
                        val bis = BufferedInputStream(fis)
                        val dis = DataInputStream(bis)

                        // Read the file into the musicarray.
                        var i = 0
                        while (dis.available() > 0) {
                            music[i] = dis.readShort()
                            i++
                        }
                        dis.close()
                        val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC,
                                frequency,
                                AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                musicLength * 2,
                                AudioTrack.MODE_STREAM)
                        // Start playback
                        audioTrack.play()

                        // Write the music buffer to the AudioTrackobject
                        audioTrack.write(music, 0, musicLength)

                        audioTrack.stop()
                    } catch (t: Throwable) {
                        Log.e("AudioTrack", t.message)
                        Log.e("AudioTrack", t.localizedMessage)
                        toast("Error: ${t.localizedMessage}")
                    }

                }
            }.start()
        }
    }
}

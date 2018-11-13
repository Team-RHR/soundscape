package com.example.kkgroup.soundscape_v2.activity

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kkgroup.soundscape_v2.model.AudioFiles
import com.example.kkgroup.soundscape_v2.model.GlobalModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import kotlinx.android.synthetic.main.activity_audio_files_list.*
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.row_audio_file.view.*
import java.io.File
import java.util.*

class ListPreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_files_list)

        val listView = findViewById<ListView>(R.id.sound_listView)
        val fileFolder = intent.extras.getString("test")

        listView.adapter = audioAdapter(this, fileFolder)

        GlobalModel.audioFiles.clear()
        getAudioFiles(File(Tools.getSoundScapePath() + fileFolder + File.separator))

        my_button.text = "idk test"
    }


    private class audioAdapter(context: Context, folder: String) : BaseAdapter() {

        private val mContext: Context
        private val folder = folder

        var mediaPlayer: MediaPlayer? = MediaPlayer()

        init {
            this.mContext = context
        }

        //Number of rows
        override fun getCount(): Int {
            return GlobalModel.audioFiles.size
        }

        // Render Row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val audioRow = layoutInflater.inflate(R.layout.row_audio_file, viewGroup, false)

            //Setting names for each row
            val nameTextView = audioRow.findViewById<TextView>(R.id.audioName)
            val name = GlobalModel.audioFiles[position].getAudioFileName()
            nameTextView.text = name

            audioRow.audio_list_button.setImageResource(R.drawable.ic_play_circle_filled_black)

            val playButton = audioRow.findViewById(R.id.audio_list_button) as ImageButton

            playButton.setOnClickListener {
                val filePath = Tools.getSoundScapePath() + folder + File.separator + nameTextView.text

                    if (mediaPlayer?.isPlaying!!) {
                        mediaPlayer?.stop()
                        mediaPlayer?.reset()
                        audioRow.audio_list_button.setImageResource(R.drawable.ic_play_circle_filled_black)
                    } else {
                        mediaPlayer?.setDataSource(filePath)
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        audioRow.audio_list_button.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp)
                    }

                }
            return audioRow
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getItem(p0: Int): Any {
            return "Test"
        }

    }


    fun getAudioFiles(root: File) {
        val fileList: ArrayList<File> = ArrayList()
        val listAllFiles = root.listFiles()

        if (listAllFiles != null && listAllFiles.isNotEmpty()) {

            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".mp3") || currentFile.name.endsWith(".jpg")) {
                    // File absolute path
                    Log.e("downloadFilePath", currentFile.getAbsolutePath())
                    // File Name
                    Log.e("downloadFileName", currentFile.getName())
                    fileList.add(currentFile.absoluteFile)
                    var fileName = currentFile.getName()

                    GlobalModel.audioFiles.add(AudioFiles(fileName))
                }
            }
            Log.w("fileList", "" + fileList.size)
        }
    }
}




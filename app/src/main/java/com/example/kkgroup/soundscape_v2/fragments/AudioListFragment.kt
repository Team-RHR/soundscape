package com.example.kkgroup.soundscape_v2.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.kkgroup.soundscape_v2.activity.PlayActivity
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.activity.AudioFilesActivity
import com.example.kkgroup.soundscape_v2.activity.SelectAudioActivity
import com.example.kkgroup.soundscape_v2.model.AudioFiles
import com.example.kkgroup.soundscape_v2.model.GlobalModel
import kotlinx.android.synthetic.main.activity_audio_files_list.*
import kotlinx.android.synthetic.main.fragment_audio_list.*
import kotlinx.android.synthetic.main.row_audio_file.view.*
import org.jetbrains.anko.image
import java.io.File
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.activity.ListPreviewActivity

class AudioListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_audio_files_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val listView = view.findViewById<ListView>(R.id.sound_listView)
        listView.adapter = audioAdapter(view.context)

        //Getting audio file names
        /*
        var gpath: String = Environment.getExternalStorageDirectory().absolutePath
        var spath = "download"
        var spath = "download/Sample Pictures"
        var fullpath = File(gpath + File.separator + spath)
        */


        //Clear the array from the previous folders/mp3 so no duplicates, I will improve this code later just temporary solution
        GlobalModel.audioFiles.clear()
        getAudioFiles(File(Tools.getSoundScapePath()))


        /*
        my_button.setOnClickListener {
            loadFragment(RecordingFragment())
        }
        */


    }

    private fun loadFragment(frag1 : RecordingFragment) {
        val ft = fragmentManager?.beginTransaction()
        ft?.replace(R.id.viewPager, frag1)
        ft?.commit()
    }


    private class audioAdapter(context: Context): BaseAdapter() {

        private val mContext: Context

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

            audioRow.audio_list_button.setImageResource(R.drawable.ic_folder_open_black_24dp)


            audioRow.setOnClickListener {



                // use file from "raw"- folder for testing purposes
                var audioFile = R.raw.muscle_car
                val playIntent = Intent(mContext, ListPreviewActivity::class.java)
                val fileFolder = nameTextView.text
                playIntent.putExtra("test", fileFolder)
                mContext.startActivity(playIntent)

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

        if (listAllFiles != null && listAllFiles.size > 0) {
            for (currentFile in listAllFiles) {
                if (!currentFile.name.endsWith(".jpg")) {
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

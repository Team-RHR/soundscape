package com.example.kkgroup.soundscape_v2.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.activity.ListPreviewActivity
import com.example.kkgroup.soundscape_v2.adapter.AudioCategoryAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

class AudioListFragment : Fragment() {

    private val LOADING_DURATION = 2000
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioCategoryAdapter: AudioCategoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_audio_categories_of_backend, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        recyclerView = view.findViewById(R.id.recyclerView)

        loadingAndDisplayContent()
    }

    private fun loadingAndDisplayContent() {
        val lytProgress = view!!.findViewById<LinearLayout>(R.id.lyt_progress)
        lytProgress.visibility = View.VISIBLE
        lytProgress.alpha = 1.0f
        recyclerView.visibility = View.GONE

        Handler().postDelayed({ Tools.viewFadeOut(lytProgress) }, LOADING_DURATION.toLong())
        Handler().postDelayed({ initComponents() }, (LOADING_DURATION + 400).toLong())
    }

    private fun initComponents() {
        recyclerView.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val items = ArrayList<String>()
        items.add("Nature")
        items.add("Human")
        items.add("Bird")
        items.add("Water")
        mAudioCategoryAdapter = AudioCategoryAdapter(context!!, items, ItemAnimation.FADE_IN)
        recyclerView.adapter = mAudioCategoryAdapter
        mAudioCategoryAdapter.notifyDataSetChanged()

        initListeners()
    }

    private fun initListeners() {
        mAudioCategoryAdapter.setOnItemClickListener { view, categoryName, position ->
            Tools.toastShow(context!!, "$categoryName clicked")
            startActivity<ListPreviewActivity>()
        }
    }


//    private fun loadFragment(frag1 : RecordingFragment) {
//        val ft = fragmentManager?.beginTransaction()
//        ft?.replace(R.id.viewPager, frag1)
//        ft?.commit()
//    }
//    private class AudioCategoryAdapter(context: Context): BaseAdapter() {
//        private val mContext: Context
//        init {
//            this.mContext = context
//        }
//        //Number of rows
//        override fun getCount(): Int {
//            return GlobalModel.audioFiles.size
//        }
//        // Render Row
//        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
//            val layoutInflater = LayoutInflater.from(mContext)
//            val audioRow = layoutInflater.inflate(R.layout.row_audio_file, viewGroup, false)
//            //Setting names for each row
//            val nameTextView = audioRow.findViewById<TextView>(R.id.audioName)
//            val name = GlobalModel.audioFiles[position].getAudioFileName()
//            nameTextView.text = name
//            audioRow.audio_list_button.setImageResource(R.drawable.ic_folder_open_black_24dp)
//            audioRow.setOnClickListener {
//                // use file from "raw"- folder for testing purposes
////                var audioFile = R.raw.muscle_car
////                val playIntent = Intent(mContext, ListPreviewActivity::class.java)
////                val fileFolder = nameTextView.text
////                playIntent.putExtra("test", fileFolder)
////                mContext.startActivity(playIntent)
//
//                mContext.startActivity<ListPreviewActivity>()
//            }
//            return audioRow
//        }
//        override fun getItemId(p0: Int): Long {
//            return p0.toLong()
//        }
//        override fun getItem(p0: Int): Any {
//            return "Test"
//        }
//    }
//    fun getAudioFiles(root: File) {
//        val fileList: ArrayList<File> = ArrayList()
//        val listAllFiles = root.listFiles()
//        if (listAllFiles != null && listAllFiles.isNotEmpty()) {
//            for (currentFile in listAllFiles) {
//                if (!currentFile.name.endsWith(".3gp")) {
//                    // File absolute path
//                    Log.e("downloadFilePath", currentFile.getAbsolutePath())
//                    // File Name
//                    Log.e("downloadFileName", currentFile.getName())
//                    fileList.add(currentFile.absoluteFile)
//                    var fileName = currentFile.getName()
//                    GlobalModel.audioFiles.add(AudioFiles(fileName))
//                }
//            }
//            Log.w("fileList", "" + fileList.size)
//        }
//    }
}
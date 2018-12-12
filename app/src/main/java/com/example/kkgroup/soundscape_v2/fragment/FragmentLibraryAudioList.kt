package com.example.kkgroup.soundscape_v2.fragment

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.Model.AudioCardModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.R.id.fabBack
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.activity.PlayActivity
import com.example.kkgroup.soundscape_v2.adapter.AudioItemAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import kotlinx.android.synthetic.main.fragment_library_audio_list.*
import org.jetbrains.anko.support.v4.startActivity
import java.io.File


class FragmentLibraryAudioList : Fragment() {

    private val LOADING_DURATION = 500
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioItemAdapter: AudioItemAdapter
    private lateinit var categoryName: String

    companion object {
        fun newInstance(): FragmentLibraryAudioList {
            return FragmentLibraryAudioList()
        }

        private var myAddToTrackListener: addToTrackListener? = null
        fun setMyAddToTrackListener(myAddToTrackListener: addToTrackListener) {
            this.myAddToTrackListener = myAddToTrackListener
        }

    }

    interface addToTrackListener {
        fun addToTrack(trackNum: Int, audioCardModel: AudioCardModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_library_audio_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListeners()
        loadingAndDisplayContent(view)
    }

    private fun loadingAndDisplayContent(view: View) {

        categoryName = arguments!!.getString("obj")

        recyclerView = view.findViewById(R.id.recyclerView)
        val lytProgress = view.findViewById<LinearLayout>(R.id.lyt_progress)
        lytProgress.visibility = View.VISIBLE
        lytProgress.alpha = 1.0f
        recyclerView.visibility = View.GONE

        Handler().postDelayed({ Tools.viewFadeOut(lytProgress) }, LOADING_DURATION.toLong())

        Handler().postDelayed({ initComponents(view) }, (LOADING_DURATION + 400).toLong())
    }

    private fun initComponents(view: View) {
        recyclerView.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        val audioFilesByCategory = if (categoryName == "My Recordings") {
            Tools.getMyRecordingsFiles(Tools.getMyRecordingPath())
        } else {
            Tools.getRemoteAudioFiles(
                    Tools.getDownloadedAudioByCategoryPath(categoryName))
        }

        //set data and list adapter
        mAudioItemAdapter = AudioItemAdapter(context!!, audioFilesByCategory, ItemAnimation.FADE_IN)
        recyclerView.adapter = mAudioItemAdapter

        // on item list clicked
        mAudioItemAdapter.setOnItemClickListener(object : AudioItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, file: File, position: Int) {
                startActivity<PlayActivity>("obj" to file,
                        "category" to categoryName,
                        "title" to file.name)
            }
        })

        mAudioItemAdapter.setOnAddToTrackListener(object : AudioItemAdapter.OnAddToTrackListener {
            override fun onItemClick(view: View, file: File, position: Int) {
                showSingleChoiceDialog(file, view as ImageView)
            }
        })
    }

    private var single_choice_selected: String? = null
    private val trackNumbers = arrayOf("1", "2")
    private val trackStrs = arrayOf("Track 1", "Track 2")
    private fun showSingleChoiceDialog(file: File, imageView: ImageView) {
        single_choice_selected = trackNumbers[0]
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Add this audio to")
        builder.setSingleChoiceItems(trackStrs, 0) { _, i ->
            single_choice_selected = trackNumbers[i]
        }
        builder.setPositiveButton("OK") { _, i ->

            var bgColor = R.color.blue_700
            when(categoryName){
                "Human" -> { bgColor = R.color.teal_700 }
                "Machine" -> { bgColor = R.color.blue_700 }
                "Nature" -> { bgColor = R.color.green_700 }
                "My Recordings" -> {
                    bgColor = R.color.deep_orange_500
                    categoryName = "Recording"
                }
            }
            val audioCardModel = AudioCardModel(categoryName, file, bgColor)

            myAddToTrackListener?.addToTrack(single_choice_selected!!.toInt(), audioCardModel)
            imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check))
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }


    private fun initListeners() {

        fabBack.setOnClickListener {
            val trans = fragmentManager!!.beginTransaction()
            trans.replace(R.id.root_frame, FragmentLibraryCategory.newInstance())
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            trans.addToBackStack(null)
            trans.commit()
        }

    }

}
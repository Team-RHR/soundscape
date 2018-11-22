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
import com.example.kkgroup.soundscape_v2.activity.PlayActivity
import com.example.kkgroup.soundscape_v2.activity.RemoteAudioFilesActivity
import com.example.kkgroup.soundscape_v2.adapter.AudioCategoryAdapter
import com.example.kkgroup.soundscape_v2.adapter.AudioItemAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import org.jetbrains.anko.support.v4.startActivity
import java.io.File
import java.util.*

class CategoryListFragment : Fragment() {

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

        // TODO Replace with real category list
        val items = ArrayList<String>()
        items.add("Human")
        items.add("Machine")
        items.add("Nature")
        mAudioCategoryAdapter = AudioCategoryAdapter(context!!, items, ItemAnimation.FADE_IN)
        recyclerView.adapter = mAudioCategoryAdapter
        mAudioCategoryAdapter.notifyDataSetChanged()

        initListeners()
    }

    private fun initListeners() {
        mAudioCategoryAdapter.setOnItemClickListener(object : AudioCategoryAdapter.OnItemClickListener {
            override fun onItemClick(view: View, categoryName: String, position: Int) {
                startActivity<RemoteAudioFilesActivity>("obj" to categoryName)
            }
        })
    }
}
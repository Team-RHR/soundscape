package com.example.kkgroup.soundscape_v2.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.AudioItemAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import org.jetbrains.anko.startActivity
import java.io.File

class RemoteAudioFilesActivity : AppCompatActivity() {

    private val LOADING_DURATION = 2000

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioItemAdapter: AudioItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        recyclerView = findViewById(R.id.recyclerView)
        initToolbar()
        initListeners()
    }

    private fun initListeners() {

    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = null
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.colorPrimary)

        loadingAndDisplayContent()
    }

    private fun loadingAndDisplayContent() {
        val lytProgress = findViewById<LinearLayout>(R.id.lyt_progress)
        lytProgress.visibility = View.VISIBLE
        lytProgress.alpha = 1.0f
        recyclerView.visibility = View.GONE

        Handler().postDelayed({ Tools.viewFadeOut(lytProgress) }, LOADING_DURATION.toLong())

        Handler().postDelayed({ initComponent() }, (LOADING_DURATION + 400).toLong())
    }

    private fun initComponent() {
        recyclerView.visibility = View.VISIBLE
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val localAudioFiles = Tools.getLocalAudioFiles(Tools.getSoundScapePath())

        //set data and list adapter
        mAudioItemAdapter = AudioItemAdapter(this, localAudioFiles, ItemAnimation.FADE_IN)
        recyclerView.adapter = mAudioItemAdapter

        // on item list clicked
        mAudioItemAdapter.setOnItemClickListener(object : AudioItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, file: File, position: Int) {
                startActivity<PlayActivity>("obj" to file)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_refresh_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish();
        } else if (item.itemId == R.id.action_refresh) {
            loadingAndDisplayContent()
        } else {
            Tools.toastShow(this, getString(R.string.toast_added_later))
        }
        return super.onOptionsItemSelected(item)
    }
}




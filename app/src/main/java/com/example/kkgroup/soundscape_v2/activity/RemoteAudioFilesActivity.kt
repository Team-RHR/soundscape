package com.example.kkgroup.soundscape_v2.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.AudioItemAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import org.jetbrains.anko.startActivity
import java.io.File

class RemoteAudioFilesActivity : AppCompatActivity() {
    private val LOADING_DURATION = 2000


    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioItemAdapter: AudioItemAdapter
    private lateinit var categoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager(this).getLocale()
        setContentView(R.layout.activity_preview)

        categoryName = intent.extras["obj"] as String
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

        val audioFilesByCategory = Tools.getRemoteAudioFiles(
                Tools.getDownloadedAudioByCategoryPath(categoryName))

        //set data and list adapter
        mAudioItemAdapter = AudioItemAdapter(this, audioFilesByCategory, ItemAnimation.FADE_IN)
        recyclerView.adapter = mAudioItemAdapter

        // on item list clicked
        mAudioItemAdapter.setOnItemClickListener(object : AudioItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, file: File, position: Int) {
                startActivity<PlayActivity>("obj" to file)
            }
        })

        mAudioItemAdapter.setOnMoreButtonClickListener(object : AudioItemAdapter.OnMoreButtonClickListener {
            override fun onItemClick(view: View, file: File, menuItem: MenuItem) {
                when(menuItem.itemId) {
                    R.id.action_play -> {
                        startActivity<PlayActivity>("obj" to file)
                    }

                    R.id.action_add_to_track -> {
                        Tools.toastShow(this@RemoteAudioFilesActivity, " add to track")

//                        mAddToTrackListener?.addToTrack(file)
//
//                        val intent = Intent(this@RemoteAudioFilesActivity,
//                                NewSoundscapeActivity::class.java).setFlags(
//                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

                        // intent.putExtra("obj", file)

                       // startActivity(intent)
                    }

                    R.id.action_delete -> {
                        Tools.toastShow(this@RemoteAudioFilesActivity, "still working on it ")
                    }
                }

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




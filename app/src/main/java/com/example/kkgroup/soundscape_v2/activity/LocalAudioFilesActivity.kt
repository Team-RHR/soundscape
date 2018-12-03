package com.example.kkgroup.soundscape_v2.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ActionMenuView
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager
import com.example.kkgroup.soundscape_v2.Tools.PrefManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.AudioItemAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import kotlinx.android.synthetic.main.activity_audio_files_of_local.*
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.actionMenuView
import org.jetbrains.anko.startActivity
import java.io.File


private val REQUEST_WRITE_EXTERNAL_PERMISSION = 200
class LocalAudioFilesActivity : AppCompatActivity() {

    private val LOADING_DURATION = 2000
    private var mExitTime: Long = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioItemAdapter: AudioItemAdapter
    private var localAudioFiles: MutableList<File>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager(this).getLocale()
        setContentView(R.layout.activity_audio_files_of_local)

        requestPermission()

        recyclerView = findViewById(R.id.recyclerView)
        initToolbar()
        initListeners()
    }

    private fun requestPermission() {

        if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            // No permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ,REQUEST_WRITE_EXTERNAL_PERMISSION)
        }else{
            // have permission
        }
    }

    override fun onResume() {
        super.onResume()
       // Tools.updateAudioFiles()
    }

    private fun initListeners() {
        fabInMyFilesPage.setOnClickListener { startActivity<NewSoundscapeActivity>() }
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Soundscape V2"
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

        if (localAudioFiles != null) {
            localAudioFiles = Tools.getLocalAudioFiles(Tools.getMyRecordingPath())

            //set data and list adapter
            // mAudioItemAdapter = AudioItemAdapter(this, localAudioFiles!!, ItemAnimation.FADE_IN)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_refresh_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            loadingAndDisplayContent()
        } else if(item.itemId == R.id.action_settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            //intent.putExtra("keyIdentifier", value)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * When back button on the phone is pressed
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Tools.toastShow(this@LocalAudioFilesActivity, getString(R.string.toast_press_again_exit))
                mExitTime = System.currentTimeMillis()
            } else {
                Tools.toastCancel()
                moveTaskToBack(true)
                // finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_PERMISSION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // user granted the permission

                    localAudioFiles = Tools.getLocalAudioFiles(Tools.getMyRecordingPath())

                    //set data and list adapter
                    mAudioItemAdapter = AudioItemAdapter(this, localAudioFiles!!, ItemAnimation.FADE_IN)
                    recyclerView.adapter = mAudioItemAdapter

                    // on item list clicked
                    mAudioItemAdapter.setOnItemClickListener(object : AudioItemAdapter.OnItemClickListener {
                        override fun onItemClick(view: View, file: File, position: Int) {
                            startActivity<PlayActivity>("obj" to file)
                        }
                    })


                } else {
                    // user denied the permission
                    Tools.toastShow(this, getString(R.string.toast_permission_denied))
                    finish()
                }
                return
            }
        }
    }
}




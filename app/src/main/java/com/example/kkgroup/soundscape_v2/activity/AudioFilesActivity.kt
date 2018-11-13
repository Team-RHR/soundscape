package com.example.kkgroup.soundscape_v2.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.model.AudioFiles
import com.example.kkgroup.soundscape_v2.model.GlobalModel
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.AudioItemAdapter
import com.example.kkgroup.soundscape_v2.model.AudioFile
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import kotlinx.android.synthetic.main.activity_audio_files_of_local.*
import org.jetbrains.anko.startActivity
import java.io.File
import java.util.*

class AudioFilesActivity : AppCompatActivity() {

    private val LOADING_DURATION = 2000
    private var mExitTime: Long = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioItemAdapter: AudioItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_files_of_local)

        recyclerView = findViewById(R.id.recyclerView)
        initToolbar()
        initListeners()
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
        val localAudioFiles = Tools.getLocalAudioFiles(Tools.getSoundScapePath())

        //set data and list adapter
        mAudioItemAdapter = AudioItemAdapter(this, localAudioFiles, ItemAnimation.FADE_IN)
        recyclerView.adapter = mAudioItemAdapter

        // on item list clicked
        mAudioItemAdapter.setOnItemClickListener { view, file, position ->
            startActivity<PlayActivity>("obj" to file)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_refresh_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            loadingAndDisplayContent()
        } else {
            Tools.toastShow(this,"Maybe will added later")
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * When back button on the phone is pressed
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Tools.toastShow(this@AudioFilesActivity, "Press again to exit")
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

            //Options button shit
            val optionsButton = audioRow.findViewById(R.id.audio_list_button) as ImageButton

            optionsButton.setOnClickListener {
                GlobalModel.audioFiles.removeAt(position)
                Toast.makeText(mContext, "Removed position: $position ", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged()
            }

            audioRow.setOnClickListener {


                var audioFile = Tools.getSoundScapePath() + name
                val playIntent = Intent(mContext, PlayActivity::class.java)
                playIntent.putExtra("audio", audioFile)

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

    private fun showPopup(view: View) {

        var popup: PopupMenu? = null;
        popup = PopupMenu(this, view)
        popup.inflate(R.menu.audio_list_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.item1 -> {
                    Toast.makeText(this@AudioFilesActivity, item.title, Toast.LENGTH_SHORT).show();
                }
                R.id.item2 -> {
                    Toast.makeText(this@AudioFilesActivity, item.title, Toast.LENGTH_SHORT).show();
                }
                R.id.item3 -> {
                    Toast.makeText(this@AudioFilesActivity, item.title, Toast.LENGTH_SHORT).show();
                }
            }

            true
        })

        popup.show()
    }

    fun getAudioFiles(root: File) {
        val fileList: ArrayList<File> = ArrayList()
        val listAllFiles = root.listFiles()

        if (listAllFiles != null && listAllFiles.isNotEmpty()) {

            GlobalModel.audioFiles.clear()      /* fixed, avoid add the same file more times when jump to this activity again */

            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".3gp")) {
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




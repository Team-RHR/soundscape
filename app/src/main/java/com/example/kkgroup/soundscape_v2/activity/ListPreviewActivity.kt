package com.example.kkgroup.soundscape_v2.activity

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.*
import android.widget.*
import com.example.kkgroup.soundscape_v2.model.AudioFiles
import com.example.kkgroup.soundscape_v2.model.GlobalModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.AudioItemAdapter
import com.example.kkgroup.soundscape_v2.model.AudioFile
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import kotlinx.android.synthetic.main.row_audio_file.view.*
import org.jetbrains.anko.startActivity
import java.io.File
import java.util.*

class ListPreviewActivity : AppCompatActivity() {

    private var parent_view: View? = null
    private val LOADING_DURATION = 2000
    private var mExitTime: Long = 0

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAudioItemAdapter: AudioItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_preview)

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
        mAudioItemAdapter.setOnItemClickListener { view, file, position ->
            startActivity<PlayActivity>("obj" to file)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_refresh_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.itemId == R.id.action_refresh) {
            loadingAndDisplayContent()
        } else {
            Tools.toastShow(this,"Maybe will added later")
        }
        return super.onOptionsItemSelected(item)
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
                if (currentFile.name.endsWith(".3gp") || currentFile.name.endsWith(".jpg")) {
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




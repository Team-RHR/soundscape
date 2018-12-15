package com.example.kkgroup.soundscape_v2.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.AudioUtils
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.activity_play.*
import java.io.File

/**
 * description: This activity is used to preview audio in a seperate page
 * create time: 13:49 2018/12/15
 */
class PlayActivity : AppCompatActivity() {

    private lateinit var parent_view: View
    private lateinit var seek_song_progressbar: AppCompatSeekBar
    private lateinit var bt_play: FloatingActionButton
    private lateinit var tv_song_current_duration: TextView
    private lateinit var tv_song_total_duration: TextView
    private lateinit var image: CircularImageView
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var targetFile: File
    private lateinit var category: String
    private lateinit var title: String
    /**
     * Handler to update UI timer, progress bar etc,.
     */
    private val mHandler = Handler()
    private var currentPos: Int = 0

    /**
     * initialize which file is gonna be playing
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager(this).getLocale()
        setContentView(R.layout.activity_play)
        targetFile = intent.extras["obj"] as File
        category = intent.extras["category"] as String
        title = intent.extras["title"] as String
        initToolbar()
        initComponents()
        initListeners()
    }

    private fun initComponents() {

        tv_title.text = title
        tv_category.text = category

        parent_view = findViewById(R.id.parent_view)
        seek_song_progressbar = findViewById(R.id.seek_song_progressbar)
        bt_play = findViewById(R.id.bt_play)

        /**
         * set Progress bar values
         */
        seek_song_progressbar.progress = 0
        seek_song_progressbar.max = AudioUtils.MAX_PROGRESS
        tv_song_current_duration = findViewById(R.id.tv_song_current_duration)
        tv_song_total_duration = findViewById(R.id.tv_song_total_duration)
        image = findViewById(R.id.image)

        /**
         *  Initialize Media Player
         */
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer = MediaPlayer.create(this, Uri.parse(targetFile.absolutePath))
            mediaPlayer.duration
            mediaPlayer.prepare()
        } catch (e: Exception) {
            Tools.log_e("Cannot load audio file, error: ${e.localizedMessage}")
        }

        /**
         * invoked when the playing is over, reset the icon
         */
        mediaPlayer.setOnCompletionListener {
            bt_play.setImageResource(R.drawable.ic_play_arrow)
        }

        /**
         *  handle the seek bar drag event
         */
        seek_song_progressbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) { }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                /**
                 * remove message Handler from updating progress bar
                 */
                mHandler.removeCallbacks(mUpdateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mHandler.removeCallbacks(mUpdateTimeTask)
                val totalDuration = mediaPlayer.duration
                val currentPosition = AudioUtils.progressToTimer(seekBar.progress, totalDuration)
                /**
                 * forward or backward to certain seconds
                 * update timer progress again
                 */
                mediaPlayer.seekTo(currentPosition)
                mHandler.post(mUpdateTimeTask)
            }
        })
        buttonPlayerAction()
        updateTimerAndSeekbar()
    }

    /**
     * Set up Tool bar
     */
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        Tools.setSystemBarColor(this, R.color.grey_1000)
    }

    private fun getAudioDuration(player: MediaPlayer) : String{
        val musicTime = player.duration / 1000
        return "${musicTime / 60}:${musicTime % 60}"
    }

    /**
     * Play button click event plays a song and changes button to pause playBtn
     * pauses a song and changes button to play playBtn
     */
    private fun buttonPlayerAction() {
        bt_play.setOnClickListener {
            // check for already playing
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                // Changing button playBtn to play button
                bt_play.setImageResource(R.drawable.ic_play_arrow)
            } else {
                // Resume song
                mediaPlayer.start()
                // Changing button playBtn to pause button
                bt_play.setImageResource(R.drawable.ic_pause)
                // Updating progress bar
                mHandler.post(mUpdateTimeTask)
            }
            rotateImageAlbum()
        }
    }

    private fun initListeners() {

        /**
         * backwards 5 seconds
         */
        bt_prev.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                currentPos = mediaPlayer.currentPosition
                mediaPlayer.seekTo(currentPos - 5000)
                mediaPlayer.start()
            }
        }

        /**
         * forwards 5 seconds
         */
        bt_next.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                currentPos = mediaPlayer.currentPosition
                mediaPlayer.seekTo(currentPos + 5000)
                mediaPlayer.start()
            }
        }
    }

    /**
     * Background Runnable thread for updating timer and seek bar
     */
    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            updateTimerAndSeekbar()
            /**
             * Running this thread after 10 milliseconds
             */
            if (mediaPlayer.isPlaying) {
                mHandler.postDelayed(this, 100)
            }
        }
    }

    private fun updateTimerAndSeekbar() {
        val totalDuration = mediaPlayer.duration.toLong()
        val currentDuration = mediaPlayer.currentPosition.toLong()
        // Displaying Total Duration time
        tv_song_total_duration.text = AudioUtils.milliSecondsToTimer(totalDuration)
        // Displaying time completed playing
        tv_song_current_duration.text = AudioUtils.milliSecondsToTimer(currentDuration)
        // Updating progress bar
        seek_song_progressbar.progress = AudioUtils.getProgressSeekBar(currentDuration, totalDuration)
    }

    /**
     * Rotate Image while audio playing
     */
    private fun rotateImageAlbum() {
        if (!mediaPlayer.isPlaying) return
        image.animate().setDuration(100).rotation(image.rotation + 2f).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                rotateImageAlbum()
                super.onAnimationEnd(animation)
            }
        })
    }

    /**
     * stop player when destroy ,
     * otherwise it will cause app crashed when you leave this page , mediaPlayer.release() is important
     */
    public override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mUpdateTimeTask)
        mediaPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}

package com.example.kkgroup.soundscape_v2.activity

import android.content.Context
import android.graphics.Rect
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.Model.AudioCardModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.ConstantValue
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.widget.MyLinearLayout
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.VerticalRangeSeekBar
import kotlinx.android.synthetic.main.activity_new_soundscape.*
import org.jetbrains.anko.startActivity
import java.io.File

class NewSoundscapeActivity : AppCompatActivity(), View.OnLongClickListener,
        MyLinearLayout.VerticalPositionDetectListener{

    override fun handleViewVerticalPostion(view: View) {

        val audioCardModel = view.tag as AudioCardModel
        if (audioCardModelList.contains(audioCardModel)) {
            audioCardModel.leftPosition = view.left
            audioCardModel.topPosition = view.top
            audioCardModel.rightPosition = view.right
            audioCardModel.bottomPosition = view.bottom
            getOrderOfAudioCards()
        }
    }

    private lateinit var seekBar: VerticalRangeSeekBar
    private lateinit var audioTrack01: MyLinearLayout
    private lateinit var audioTrack02: MyLinearLayout
    private var audioCardViewListForTrack01 = mutableListOf<View>()
    private var audioCardViewListForTrack02 = mutableListOf<View>()
    private var audioCardViewList = mutableListOf<View>()

    val audioCardModelList = mutableListOf<AudioCardModel>()
    private var mVibrator: Vibrator? = null
    private var isPlaying = false
    private var selectedFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager(this).getLocale()
        setContentView(R.layout.activity_new_soundscape)

        initToolbar()
        initComponents()
        initListeners()

        generateAudioCard(1)
        generateAudioCard(1)
        generateAudioCard(2)
    }

    private var isFirst = true
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isFirst) {

            for (audioCardView in audioCardViewListForTrack01) {
                val audioCardModel = AudioCardModel(trackNum = 1, leftPosition = audioCardView.left,
                        topPosition =  audioCardView.top, bottomPosition = audioCardView.bottom,
                        rightPosition = audioCardView.right, isDraggable = true)
                audioCardView.tag = audioCardModel
                audioCardModelList.add(audioCardModel)
                // Tools.log_e("audioCardView Track 01: ${audioCardView.top} --- ${audioCardView.bottom}")
            }

            for (audioCardView in audioCardViewListForTrack02) {
                val audioCardModel = AudioCardModel(trackNum = 2, leftPosition = audioCardView.left,
                        topPosition =  audioCardView.top, bottomPosition = audioCardView.bottom,
                        rightPosition = audioCardView.right, isDraggable = true)
                audioCardView.tag = audioCardModel
                audioCardModelList.add(audioCardModel)
                // Tools.log_e("audioCardView Track 02: ${audioCardView.top} --- ${audioCardView.bottom}")
            }

            isFirst = false
        }

    }

    private fun getOrderOfAudioCards() {

        val isOverlapping = isViewOverlapping(audioCardViewListForTrack01[0], audioCardViewListForTrack01[1])
        Tools.log_e("isOverlapping: $isOverlapping")

        audioCardModelList.sortBy { it.topPosition }
        audioCardModelList.forEach {
            // Tools.log_e(it.toString())
        }
    }

    private fun isViewOverlapping(firstView: View, secondView: View): Int {

        val rectFirstView = getRectOfAudioCard(firstView)
        val rectSecondView = getRectOfAudioCard(secondView)

        val rectFirstViewTop = rectFirstView.top
        val rectSecondViewTop = rectSecondView.top
         Tools.log_e("first: ${rectFirstViewTop}")
         Tools.log_e("second: ${rectSecondViewTop}")

        if (rectFirstView.intersect(rectSecondView)) {

            val dy = (rectFirstViewTop - rectSecondViewTop).toDouble()

            val overlapRatio = 1 - Math.abs(( dy / firstView.measuredHeight ))

            Tools.toastShow(this, "Overlap Ratio: ${overlapRatio}")

            if ( dy < 0 ) {
                Tools.log_e("01 在前面, 重叠的比例: $overlapRatio, 01.top: ${rectFirstView.top}, 02.top : ${rectSecondView.top}" +
                        " dy: $dy")
            } else {
                Tools.log_e("02 在前面, 重叠的比例: $overlapRatio, 01.top: ${rectFirstView.top}, 02.top : ${rectSecondView.top}" +
                        "height: ${firstView.measuredHeight} dy: $dy")
            }

            return 1
        } else {
            return 0
        }
    }

    /**
     * Rect constructor parameters: left, top, right, bottom
     */
    private fun getRectOfAudioCard(view: View): Rect {

        val audioCardModel = view.tag as AudioCardModel

        return Rect(audioCardModel.leftPosition, audioCardModel.topPosition,
                audioCardModel.rightPosition, audioCardModel.bottomPosition)
    }


            /**
             * flag -> 1 : add audio card to track 01
             *         2 : add audio card to track 02
             */
            private

    fun generateAudioCard(flag: Int) {

        if (flag == 1) {
            val audioCard = LayoutInflater.from(this)
                    .inflate(R.layout.audio_file_card_item, audioTrack01, false)

            audioTrack01.addView(audioCard)
            audioCardViewListForTrack01.add(audioCard)
            audioCardViewList.add(audioCard)

            audioCard.setOnClickListener {
                showBottomSheetDialog("childView 1")
            }
            audioCard.setOnLongClickListener(this)


        } else {
            val audioCard = LayoutInflater.from(this)
                    .inflate(R.layout.audio_file_card_item, audioTrack02, false)

            audioTrack02.addView(audioCard)
            audioCardViewListForTrack02.add(audioCard)
            audioCardViewList.add(audioCard)

            audioCard.setOnClickListener {
                showBottomSheetDialog("childView 2")
            }
            audioCard.setOnLongClickListener(this)
        }

    }

    /**
     * The following method requires SDK >= 26, Our Target SDK is 21
     * We are lazy to do SDK check, so we just use deprecated method , let me go this time :)
     */
    override fun onLongClick(view: View?): Boolean {
        mVibrator?.let {
            // if (it.hasVibrator()) it.vibrate(VibrationEffect.createOneShot(ConstantValue.vibrationTime, -1))
            if (it.hasVibrator()) it.vibrate(ConstantValue.vibrationTime)
            return true
        }
        return true
    }

    private fun initComponents() {
        seekBar = findViewById(R.id.myVerticalSeekbar)
        seekBar.setValue(30f)

        // make it work!
        seekBar.invalidate()

        audioTrack01 = findViewById(R.id.audio_track_one)
        audioTrack02 = findViewById(R.id.audio_track_two)
        mVibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        bottom_sheet = findViewById(R.id.bottom_sheet)
        mBehavior = BottomSheetBehavior.from(bottom_sheet)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Soundscape V2"
        Tools.setSystemBarColor(this, R.color.colorPrimary)
    }


    // Media Player
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaPlayer2: MediaPlayer
    private var isPlayedAlready = false
    private fun initListeners() {

        // Media Player 01
        mediaPlayer = MediaPlayer()
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer = MediaPlayer.create(this, Uri.parse("/storage/emulated/0/soundscape/downloads/Human/Saksofonin_soittoa_30s.mp3"))
            mediaPlayer.duration
            mediaPlayer.prepare()
        } catch (e: Exception) {
            Tools.log_e("Cannot load audio file")
        }
        mediaPlayer.setOnCompletionListener {
            ib_play.setImageResource(R.drawable.ic_play_arrow)
            Tools.log_e("mediaPlayer1 is done")
        }

        // ++++++++++++++++++++++++++++++++++
        // Media Player 01
        mediaPlayer2 = MediaPlayer()
        try {
            mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer2 = MediaPlayer.create(this, Uri.parse("/storage/emulated/0/soundscape/downloads/Human/Humalainen_porukka_15s.mp3"))

            mediaPlayer2.duration
            mediaPlayer2.prepare()
        } catch (e: Exception) {
            Tools.log_e("Cannot load audio file 02")
        }
        mediaPlayer2.setOnCompletionListener {
            // ib_play.setImageResource(R.drawable.ic_play_arrow)
            Tools.log_e("mediaPlayer2 is done")
            isPlayedAlready = true
        }




        audioTrack01.setMyVerticalPositionDetectListener(this)
        audioTrack02.setMyVerticalPositionDetectListener(this)

        ib_play.setOnClickListener {

            // check for already playing
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                // Changing button image to play button
                ib_play.setImageResource(R.drawable.ic_play_arrow)
            } else {
                // Resume song
                mediaPlayer.start()
                Tools.log_e("mediaPlayer1 is starting")
                // Changing button image to pause button
                ib_play.setImageResource(R.drawable.ic_pause)
            }

            // ++++++++++++++++++++++++++++++

            if (! isPlayedAlready) {
                // check for already playing
                if (mediaPlayer2.isPlaying) {
                    mediaPlayer2.pause()
                    // Changing button image to play button
                    // ib_play.setImageResource(R.drawable.ic_play_arrow)
                } else {
                    // Resume song
                    mediaPlayer2.start()
                    Tools.log_e("mediaPlayer2 is starting")

                    // Changing button image to pause button
                    //  ib_play.setImageResource(R.drawable.ic_pause)
                }
            }


//            getOrderOfAudioCards()
//
//            if (isPlaying) {
//                ib_play.setImageResource(R.drawable.ic_play_arrow)
//                switchDraggable(true)
//            } else {
//                ib_play.setImageResource(R.drawable.ic_pause)
//                switchDraggable(false)
//            }
//            isPlaying = !isPlaying
        }

        seekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {

                // Tools.log_e("leftValue: $leftValue --> rightValue: $rightValue")
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

        })

        // make it work!
        seekBar.invalidate()
    }

    // stop player when destroy
    public override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mediaPlayer2.release()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.stop()
        mediaPlayer2.stop()
    }

    private fun switchDraggable(isDraggable: Boolean) {

        for (audioCardView in audioCardViewListForTrack01) {

            val audioCardModel = audioCardView.tag as AudioCardModel
            audioCardModel.isDraggable = isDraggable

            audioCardView.tag = audioCardModel
        }

        for (audioCardView in audioCardViewListForTrack02) {
            val audioCardModel = audioCardView.tag as AudioCardModel
            audioCardModel.isDraggable = isDraggable

            audioCardView.tag = audioCardModel
        }
    }

    private lateinit var mBehavior: BottomSheetBehavior<View>
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var bottom_sheet: View? = null
    private fun showBottomSheetDialog(message: String) {
        if (mBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val view = layoutInflater.inflate(R.layout.sheet_audio_card_floating, null)
        (view.findViewById(R.id.name) as TextView).text = message
        (view.findViewById(R.id.brief) as TextView).text = "From human category"
        view.findViewById<ImageButton>(R.id.bt_close).setOnClickListener {
            mBottomSheetDialog?.hide()
        }

        view.findViewById<AppCompatButton>(R.id.deleteFromTrack).setOnClickListener {
            Toast.makeText(applicationContext, "Delete From Track", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<AppCompatButton>(R.id.previewAudioCard).setOnClickListener {
            Toast.makeText(applicationContext, "Preview Audio Card", Toast.LENGTH_SHORT).show()
        }

        mBottomSheetDialog = BottomSheetDialog(this)
        mBottomSheetDialog?.setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog?.getWindow()!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

        mBottomSheetDialog?.show()
        mBottomSheetDialog?.setOnDismissListener {
            mBottomSheetDialog = null
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            startActivity<SelectAudioActivity>()
        }
        return super.onOptionsItemSelected(item)
    }
}


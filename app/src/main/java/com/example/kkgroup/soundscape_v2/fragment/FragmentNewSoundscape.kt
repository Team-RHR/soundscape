package com.example.kkgroup.soundscape_v2.fragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.Tools.Tools.log_e
import com.example.kkgroup.soundscape_v2.adapter.AdapterListDrag
import com.example.kkgroup.soundscape_v2.widget.DragItemTouchHelper
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.VerticalRangeSeekBar
import java.io.File

class FragmentNewSoundscape : Fragment() {

    private lateinit var seekBar: VerticalRangeSeekBar
    private lateinit var playButton: ImageButton
    private var mediaPlayer1: MediaPlayer = MediaPlayer()
    private var mediaPlayer2: MediaPlayer = MediaPlayer()
    private lateinit var mBehavior: BottomSheetBehavior<View>
    private var isPlayedAlready = false
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var bottom_sheet: View? = null

    private lateinit var recyclerView1: RecyclerView
    private lateinit var recyclerView2: RecyclerView
    private lateinit var dragAdapter1: AdapterListDrag
    private lateinit var dragAdapter2: AdapterListDrag
    private var mItemTouchHelper1: ItemTouchHelper? = null
    private var mItemTouchHelper2: ItemTouchHelper? = null
    private val audioCardTrack1 = ArrayList<File>()
    private val audioCardTrack2 = ArrayList<File>()

    companion object {
        fun newInstance(): FragmentNewSoundscape {
            return FragmentNewSoundscape()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_soundscape, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initComponents(view)
        initFakeData()
        initListeners()
    }

    private fun initFakeData() {
        audioCardTrack1.add(File("/storage/emulated/0/soundscape/downloads/Machine/Antiikki_auto_käyntiä_30s.mp3"))
        audioCardTrack1.add(File("/storage/emulated/0/soundscape/downloads/Machine/Metro_ohiajo_30s.mp3"))
        audioCardTrack1.add(File("/storage/emulated/0/soundscape/downloads/Nature/A_Suomalaisia_lintuja_15s.mp3"))

        audioCardTrack2.add(File("/storage/emulated/0/soundscape/downloads/Nature/Hevonen_hirnuu_15s.mp3"))
        audioCardTrack2.add(File("/storage/emulated/0/soundscape/downloads/Human/Humalainen_porukka_15s.mp3"))

    }

    private fun initComponents(view: View) {

        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC)

        playButton = view.findViewById(R.id.ib_play)
        seekBar = view.findViewById(R.id.myVerticalSeekbar)
        seekBar.setValue(30f)
        seekBar.invalidate()

        bottom_sheet = view.findViewById(R.id.bottom_sheet)
        mBehavior = BottomSheetBehavior.from(bottom_sheet)

        /**
         * set data and list adapter for recycleview01 (Track 01)
         * and setup ItemTouchHelper to let item be braggable
         */
        recyclerView1 = view.findViewById(R.id.recyclerView1)
        recyclerView1.layoutManager = LinearLayoutManager(context)
        recyclerView1.setHasFixedSize(true)
        dragAdapter1 = AdapterListDrag(context!!, audioCardTrack1)
        recyclerView1.adapter = dragAdapter1

        mItemTouchHelper1 = ItemTouchHelper(DragItemTouchHelper(dragAdapter1))
        mItemTouchHelper1?.attachToRecyclerView(recyclerView1)

        /**
         * set data and list adapter for recycleview01 (Track 02)
         * and setup ItemTouchHelper to let item be braggable
         */
        recyclerView2 = view.findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = LinearLayoutManager(context)
        recyclerView2.setHasFixedSize(true)
        dragAdapter2 = AdapterListDrag(context!!, audioCardTrack2)
        recyclerView2.adapter = dragAdapter2

        mItemTouchHelper2 = ItemTouchHelper(DragItemTouchHelper(dragAdapter2))
        mItemTouchHelper2?.attachToRecyclerView(recyclerView2)

    }

    private fun initListeners() {

        dragAdapter1.setOnItemClickListener(object : AdapterListDrag.OnItemClickListener {
            override fun onItemClick(view: View, obj: File, position: Int) {
                Tools.toastShow(context!!, "Item " + obj.name + " clicked")
            }
        })

        dragAdapter1.setDragListener(object : AdapterListDrag.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                mItemTouchHelper1?.startDrag(viewHolder)
            }
        })

        dragAdapter2.setOnItemClickListener(object : AdapterListDrag.OnItemClickListener {
            override fun onItemClick(view: View, obj: File, position: Int) {
                Tools.toastShow(context!!, "Item " + obj.name + " clicked")
            }
        })

        dragAdapter2.setDragListener(object : AdapterListDrag.OnStartDragListener {
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                mItemTouchHelper2?.startDrag(viewHolder)
            }
        })

        seekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {
                // Tools.log_e("leftValue: $leftValue --> rightValue: $rightValue")
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {}

        })
        seekBar.invalidate()

        FragmentLibraryAudioList.setMyAddToTrackListener(object : FragmentLibraryAudioList.addToTrackListener {
            override fun addToTrack(trackNum: Int, file: File) {
                if (trackNum == 1) {
                    audioCardTrack1.add(file)
                    dragAdapter1.notifyDataSetChanged()
                    log_e("1 添加了一个 ${file.absolutePath}")
                } else {
                    audioCardTrack2.add(file)
                    dragAdapter2.notifyDataSetChanged()
                    log_e("2 添加了一个 ${file.absolutePath}")
                }
            }
        })

        playButton.setOnClickListener {

            log_e("size = ${audioCardTrack2.size}")
            playSound4FileList(audioCardTrack2[0].absolutePath)

            //            setUpPlayers()
//
//            if (mediaPlayer1.isPlaying) {
//                mediaPlayer1.pause()
//                playButton.setImageResource(R.drawable.ic_play_arrow)
//            } else {
//                mediaPlayer1.start()
//                Tools.log_e("mediaPlayer1 is starting")
//                playButton.setImageResource(R.drawable.ic_pause)
//            }
//
//            if (mediaPlayer2.isPlaying) {
//                mediaPlayer2.pause()
//                playButton.setImageResource(R.drawable.ic_play_arrow)
//            } else {
//                mediaPlayer2.start()
//                Tools.log_e("mediaPlayer2 is starting")
//                playButton.setImageResource(R.drawable.ic_pause)
//            }
        }
    }

//    var i = 0
//
//    private lateinit var player: MediaPlayer
//    private fun playAudio(path: String) {
//
//        player = MediaPlayer.create(context, Uri.parse(path))
//        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
//        player.prepare();
//        player.setOnCompletionListener {
//            player.stop();
//            if (i < audioCardTrack2.size) {
//                i++;
//                playAudio(audioCardTrack2[i].absolutePath);
//            } else i = 0;
//        }
//
//        player.start();
//
//    }

    var y = 0
    private fun playSound4FileList(fileList: String) {
        try {
            val mPlayerT = MediaPlayer()
            mPlayerT.setDataSource(context, Uri.parse(fileList))
            mPlayerT.prepare();
            mPlayerT.setVolume(1f, 1f);
            mPlayerT.isLooping = false;

            mPlayerT.setOnCompletionListener {
                Tools.log_e("mediaPlayer ${fileList} 播放完成")

                mPlayerT.stop();
                if (y < audioCardTrack2.size - 1) {
                    y++
                    Tools.log_e("y = ${y}")
                    playSound4FileList(audioCardTrack2[y].absolutePath)
                } else y = 0;
            }

            mPlayerT.start()
        } catch (e: Exception) {
            log_e("e: ${e.toString()}")
        }

    }

    private fun setUpPlayers() {

        // Media Player 01
//        val fileSize01 = if (audioCardTrack1.isNotEmpty()) {
//            audioCardTrack1[0].length()
//        } else {
//            0
//        }
//
//        val fileSize02 = if (audioCardTrack2.isNotEmpty()) {
//            audioCardTrack2[0].length()
//        } else {
//            0
//        }
//
//        Tools.log_e(" fileSize01 size : ${fileSize01}")
//        Tools.log_e(" fileSize02 size : ${fileSize02}")
//
//        audioCardTrack1.forEachIndexed { index, file ->
//
//        }
//
//        audioCardTrack2.forEachIndexed { index, file ->
//
//        }
//
//        if (audioCardTrack1.isNotEmpty()) {
//            val filePath = audioCardTrack1[0].absolutePath
//
//            /**
//             * init two mediaplayer
//             */
//            try {
//                mediaPlayer1 = MediaPlayer.create(context, Uri.parse(filePath))
//                mediaPlayer1.duration
//                mediaPlayer1.prepare()
//            } catch (e: Exception) {
//                Tools.log_e("Cannot load audio file 01")
//            }
//            mediaPlayer1.setOnCompletionListener {
//                playButton.setImageResource(R.drawable.ic_play_arrow)
//                Tools.log_e("media Player1 is done")
//            }
//        }
//
//        if (audioCardTrack2.isNotEmpty()) {
//            val filePath2 = audioCardTrack2[0].absolutePath
//            // Media Player 02
//            try {
//                mediaPlayer2 = MediaPlayer.create(context, Uri.parse(filePath2))
//                mediaPlayer2.duration
//                mediaPlayer2.prepare()
//            } catch (e: Exception) {
//                Tools.log_e("Cannot load audio file 02")
//            }
//            mediaPlayer2.setOnCompletionListener {
//                // ib_play.setImageResource(R.drawable.ic_play_arrow)
//                Tools.log_e("media Player2 is done")
//            }
//        }
    }

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
            Toast.makeText(context, "Delete From Track", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<AppCompatButton>(R.id.previewAudioCard).setOnClickListener {
            Toast.makeText(context, "Preview Audio Card", Toast.LENGTH_SHORT).show()
        }

        mBottomSheetDialog = BottomSheetDialog(context!!)
        mBottomSheetDialog?.setContentView(view)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog?.getWindow()!!.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        // set background transparent
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        mBottomSheetDialog?.show()
        mBottomSheetDialog?.setOnDismissListener { mBottomSheetDialog = null }
    }

    // stop player when destroy
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1.release()
        mediaPlayer2.release()
    }
}
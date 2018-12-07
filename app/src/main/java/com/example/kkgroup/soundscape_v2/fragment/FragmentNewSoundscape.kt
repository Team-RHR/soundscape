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
import android.support.v7.widget.GridLayoutManager
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
import com.example.kkgroup.soundscape_v2.adapter.ListDragAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemTouchHelperCallback
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.VerticalRangeSeekBar
import java.io.File


private const val AUDIO_TRACK_ONE = 1
private const val AUDIO_TRACK_TWO = 2

class FragmentNewSoundscape : Fragment() {

    private lateinit var seekBar: VerticalRangeSeekBar
    private lateinit var playButton: ImageButton
    private var mediaPlayer1: MediaPlayer = MediaPlayer()
    private var mediaPlayer2: MediaPlayer = MediaPlayer()
    private lateinit var mBehavior: BottomSheetBehavior<View>
    private var isPlayedAlready1 = false
    private var isPlayedAlready2 = false
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var bottom_sheet: View? = null

    // ====================
    private lateinit var recyclerView1: RecyclerView
    private lateinit var itemTouchHelper1: ItemTouchHelper
    private lateinit var listDragAdapter1: ListDragAdapter
    private val audioCardsListOnTrack1 = ArrayList<File>()

    private lateinit var recyclerView2: RecyclerView
    private lateinit var itemTouchHelper2: ItemTouchHelper
    private lateinit var listDragAdapter2: ListDragAdapter
    private val audioCardsListOnTrack2 = ArrayList<File>()

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
        audioCardsListOnTrack1.add(File("/storage/emulated/0/soundscape/downloads/Nature/A_Suomalaisia_lintuja_15s.mp3"))
        audioCardsListOnTrack1.add(File("/storage/emulated/0/soundscape/downloads/Machine/Antiikki_auto_käyntiä_30s.mp3"))
        // audioCardsListOnTrack1.add(File("/storage/emulated/0/soundscape/downloads/Machine/Metro_ohiajo_30s.mp3"))

        audioCardsListOnTrack2.add(File("/storage/emulated/0/soundscape/downloads/Nature/Hevonen_hirnuu_15s.mp3"))
        audioCardsListOnTrack2.add(File("/storage/emulated/0/soundscape/downloads/Human/Humalainen_porukka_15s.mp3"))

        initPlayers()
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
        // recyclerView1.layoutManager = GridLayoutManager(context, 2);
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        recyclerView1.setHasFixedSize(true)
        listDragAdapter1 = ListDragAdapter(context!!, audioCardsListOnTrack1)
        recyclerView1.adapter = listDragAdapter1

        val itemTouchHelperCallback = ItemTouchHelperCallback(listDragAdapter1)
        itemTouchHelper1 = ItemTouchHelper(itemTouchHelperCallback)
        listDragAdapter1.setOnItemTouchHelper(itemTouchHelper1)
        itemTouchHelper1.attachToRecyclerView(recyclerView1)


        /**
         * set data and list adapter for recycleview01 (Track 02)
         * and setup ItemTouchHelper to let item be braggable
         */
        recyclerView2 = view.findViewById(R.id.recyclerView2)
        recyclerView2.layoutManager = LinearLayoutManager(context)
        recyclerView2.setHasFixedSize(true)
        listDragAdapter2 = ListDragAdapter(context!!, audioCardsListOnTrack2)
        recyclerView2.adapter = listDragAdapter2

        val itemTouchHelperCallback2 = ItemTouchHelperCallback(listDragAdapter2)
        itemTouchHelper2 = ItemTouchHelper(itemTouchHelperCallback2)
        listDragAdapter2.setOnItemTouchHelper(itemTouchHelper2)
        itemTouchHelper2.attachToRecyclerView(recyclerView2)
    }

    private fun initListeners() {

        listDragAdapter1.setOnItemClickListener(object : ListDragAdapter.OnItemClickListener {
            override fun onItemClick(view: View, obj: File, position: Int) {
                Tools.toastShow(context!!, "Item " + obj.name + " clicked")
            }
        })

        listDragAdapter2.setOnItemClickListener(object : ListDragAdapter.OnItemClickListener {
            override fun onItemClick(view: View, obj: File, position: Int) {
                Tools.toastShow(context!!, "Item " + obj.name + " clicked")
            }
        })

        /**
         * 每添加一次和每拖拽一次都需要更新一下initPlayers()
         */
        listDragAdapter1.setOnItemsChangeListener(object : ListDragAdapter.OnItemsChangeListener {
            override fun onItemsChange(fromPosition: Int, toPosition: Int) {
                Tools.log_e("listDragAdapter1 位置进行了改变: $fromPosition --> $toPosition")
                initPlayers()
            }
        })

        listDragAdapter2.setOnItemsChangeListener(object : ListDragAdapter.OnItemsChangeListener {
            override fun onItemsChange(fromPosition: Int, toPosition: Int) {
                Tools.log_e("listDragAdapter2 位置进行了改变: $fromPosition --> $toPosition")
                initPlayers()
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
                /**
                 * 在这里每次添加一个就要刷新一遍initPlayers()
                 */
                if (trackNum == 1) {
                    audioCardsListOnTrack1.add(file)
                    listDragAdapter1.notifyDataSetChanged()
                    log_e("1 添加了一个 ${file.absolutePath}")
                } else {
                    audioCardsListOnTrack2.add(file)
                    listDragAdapter2.notifyDataSetChanged()
                    log_e("2 添加了一个 ${file.absolutePath}")
                }
            }
        })

        playButton.setOnClickListener {

            log_e("isPlayedAlready1 = ${isPlayedAlready1}")
            log_e("isPlayedAlready2 = ${isPlayedAlready2}")

            // check for already playing
            if ( ! isPlayedAlready1) {
                playerOnTrack1?.let {
                    if (it.isPlaying) {
                        it.pause()
                        isPlayingOnTrack1 = false
                    } else {
                        it.start()
                        isPlayingOnTrack1 = true
                    }
                }
            }


            if ( ! isPlayedAlready2) {
                playerOnTrack2?.let {
                    if (it.isPlaying) {
                        it.pause()
                        isPlayingOnTrack2 = false
                    } else {
                        it.start()
                        isPlayingOnTrack2 = true
                    }
                }
            }


            if (isPlayingOnTrack1 || isPlayingOnTrack2) {
                playButton.setImageResource(R.drawable.ic_pause)
            } else {
                playButton.setImageResource(R.drawable.ic_play_arrow)
            }
        }
    }

    private fun initPlayers() {

        playerList.clear()
        log_e("size 1=  ${audioCardsListOnTrack1.size}")
        log_e("size 2 = ${audioCardsListOnTrack2.size}")
        log_e("isPlayingOnTrack1 = ${isPlayingOnTrack1}")
        log_e("isPlayingOnTrack1 = ${isPlayingOnTrack2}")


        if (audioCardsListOnTrack1.size == audioCardsListOnTrack2.size &&
                ! isPlayingOnTrack1 && ! isPlayingOnTrack2) {

            log_e("准备播放01 = ${listDragAdapter1.getItems()[0].absolutePath}")
            log_e("准备播放02 = ${listDragAdapter2.getItems()[0].absolutePath}")

//            playerOnTrack1 = playThisAudioCard(AUDIO_TRACK_ONE, listDragAdapter1.getItems()[0].absolutePath)
//            playerOnTrack2 = playThisAudioCard(AUDIO_TRACK_TWO, listDragAdapter2.getItems()[0].absolutePath)
            val list:ArrayList<MediaPlayer> = playAudioRowByRow(listDragAdapter1.getItems()[0].absolutePath,
                    listDragAdapter2.getItems()[0].absolutePath)

        }
    }

    private fun playAudioRowByRow(absolutePath1: String?, absolutePath2: String?): ArrayList<MediaPlayer> {
        val players = ArrayList<MediaPlayer>()
        return players
    }

    private var isPlayingOnTrack1 = false
    private var isPlayingOnTrack2 = false
    private var playerOnTrack1: MediaPlayer? = null
    private var playerOnTrack2: MediaPlayer? = null
    private var playerList = ArrayList<MediaPlayer>()
    private fun playThisAudioCard(trackNum: Int, fileName: String): MediaPlayer {
        val player = MediaPlayer()
        try {
            player.setDataSource(context, Uri.parse(fileName))
            player.prepare();
            player.setVolume(1f, 1f);
            player.isLooping = false;

            player.setOnCompletionListener {
                Tools.log_e("mediaPlayer $fileName 播放完成")
                swapPlayingFlag(trackNum, false)

                if (trackNum == AUDIO_TRACK_ONE) {
                    isPlayedAlready1 = true
                } else {
                    isPlayedAlready2 = true
                }
            }
//            player.start()
//            swapPlayingFlag(trackNum, true)
//            playerList.add(player)
        } catch (e: Exception) {
            log_e("e: ${e.toString()}")
        }

        return player
    }

    private fun swapPlayingFlag(trackNum: Int, isPlaying: Boolean) {
        if (trackNum == AUDIO_TRACK_ONE) {
            isPlayingOnTrack1 = isPlaying
        } else {
            isPlayingOnTrack2 = isPlaying
        }

        Tools.log_e("1号音轨状态:$isPlayingOnTrack1, 2号音轨状态:$isPlayingOnTrack2")
        if (isPlayingOnTrack1 || isPlayingOnTrack2) {
            playButton.setImageResource(R.drawable.ic_pause)
        } else {
            playButton.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    var y = 0
    private fun playSound4FileList(fileList: String) {
        try {
            val mPlayerT = MediaPlayer()
            mPlayerT.setDataSource(context, Uri.parse(fileList))
            mPlayerT.prepare();
            mPlayerT.setVolume(1f, 1f);
            mPlayerT.isLooping = false;

            mPlayerT.setOnCompletionListener {
                Tools.log_e("mediaPlayer $fileList 播放完成")

                mPlayerT.stop();
                if (y < audioCardsListOnTrack2.size - 1) {
                    y++
                    Tools.log_e("y = ${y}")
                    playSound4FileList(audioCardsListOnTrack2[y].absolutePath)
                } else y = 0;
            }

            mPlayerT.start()
        } catch (e: Exception) {
            log_e("e: ${e.toString()}")
        }
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
package com.example.kkgroup.soundscape_v2.fragment

import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kkgroup.soundscape_v2.Model.AudioCardModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.Tools.Tools.log_e
import com.example.kkgroup.soundscape_v2.activity.PlayActivity
import com.example.kkgroup.soundscape_v2.activity.SearchActivity
import com.example.kkgroup.soundscape_v2.adapter.ListDragAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemTouchHelperCallback
import org.jetbrains.anko.support.v4.startActivity
import java.io.File

/**
 * description: This fragment refers to new soundscape page
 *              where you can creating your audios by adding multiple audio cards
 * create time: 14:37 2018/12/15
 */
private const val AUDIO_TRACK_ONE = 1
private const val AUDIO_TRACK_TWO = 2
class FragmentNewSoundscape : Fragment() {

    private lateinit var playButton: FloatingActionButton
    private var mediaPlayer1: MediaPlayer = MediaPlayer()
    private var mediaPlayer2: MediaPlayer = MediaPlayer()
    private var isPlayedAlready1 = false
    private var isPlayedAlready2 = false
    private var isPlayingOnTrack1 = false
    private var isPlayingOnTrack2 = false
    private var playerOnTrack1: MediaPlayer? = null
    private var playerOnTrack2: MediaPlayer? = null
    private var playerList = ArrayList<MediaPlayer>()
    private var currentIndex = 0

    /**
     * Properties for audio track one
     */
    private lateinit var recyclerView1: RecyclerView
    private lateinit var itemTouchHelper1: ItemTouchHelper
    private lateinit var listDragAdapter1: ListDragAdapter
    private lateinit var itemTouchHelperCallback :ItemTouchHelperCallback
    private val audioCardsListOnTrack1 = ArrayList<AudioCardModel>()

    /**
     * Properties for audio track two
     */
    private lateinit var recyclerView2: RecyclerView
    private lateinit var itemTouchHelper2: ItemTouchHelper
    private lateinit var listDragAdapter2: ListDragAdapter
    private lateinit var itemTouchHelperCallback2 :ItemTouchHelperCallback
    private val audioCardsListOnTrack2 = ArrayList<AudioCardModel>()
    private var maxRowCount = 0

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
        initPlayers()
        initListeners()
    }

    private fun initComponents(view: View) {

        mediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC)
        playButton = view.findViewById(R.id.fab_play)

        /**
         * set data and list adapter for recycleview01 (Track 01)
         * and setup ItemTouchHelper to let item be draggable
         */
        recyclerView1 = view.findViewById(R.id.recyclerView1)
        recyclerView1.layoutManager = LinearLayoutManager(context)
        recyclerView1.setHasFixedSize(true)
        listDragAdapter1 = ListDragAdapter(context!!, audioCardsListOnTrack1)
        recyclerView1.adapter = listDragAdapter1

        /**
         * set up touch listener for listDragAdapter1 to detect if the item is touched or not
         */
        itemTouchHelperCallback = ItemTouchHelperCallback(listDragAdapter1)
        itemTouchHelperCallback.setDraggable(true)
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

        /**
         * set up touch listener for listDragAdapter2 to detect if the item is touched or not
         */
        itemTouchHelperCallback2 = ItemTouchHelperCallback(listDragAdapter2)
        itemTouchHelperCallback2.setDraggable(true)
        itemTouchHelper2 = ItemTouchHelper(itemTouchHelperCallback2)
        listDragAdapter2.setOnItemTouchHelper(itemTouchHelper2)
        itemTouchHelper2.attachToRecyclerView(recyclerView2)
    }

    private fun initListeners() {

        /**
         * If one audio card in audio track one has been pressed,
         * then go to the preview page to play this specific audio
         */
        listDragAdapter1.setOnItemClickListener(object : ListDragAdapter.OnItemClickListener {
            override fun onItemClick(view: View, audioCardModel: AudioCardModel, position: Int) {
                startActivity<PlayActivity>("obj" to audioCardModel.file,
                        "category" to audioCardModel.category,
                        "title" to audioCardModel.file.name)
            }
        })

        /**
         * If one audio card in audio track two has been pressed,
         * then go to the preview page to play this specific audio
         */
        listDragAdapter2.setOnItemClickListener(object : ListDragAdapter.OnItemClickListener {
            override fun onItemClick(view: View, audioCardModel: AudioCardModel, position: Int) {
                startActivity<PlayActivity>("obj" to audioCardModel.file,
                        "category" to audioCardModel.category,
                        "title" to audioCardModel.file.name)
            }
        })

        /**
         * Once you change the one of the audio card's position,
         * such as drag it to up or down or delete it from the track, it will play from the start,
         * so we have to reset the players here
         */
        listDragAdapter1.setOnItemsChangeListener(object : ListDragAdapter.OnItemsChangeListener {
            override fun onItemsChange(fromPosition: Int, toPosition: Int) {
                initPlayers()
            }
        })

        /**
         * Once you change the one of the audio card's position,
         * such as drag it to up or down or delete it from the track, it will play from the start,
         * so we have to reset the players here
         */
        listDragAdapter2.setOnItemsChangeListener(object : ListDragAdapter.OnItemsChangeListener {
            override fun onItemsChange(fromPosition: Int, toPosition: Int) {
                initPlayers()
            }
        })

        /**
         * Once you change the one of the audio card's position,
         * such as drag it to up or down or delete it from the track, it will play from the start,
         * so we have to reset the players here
         */
        listDragAdapter1.setOnItemDeleteListener(object : ListDragAdapter.OnItemDeleteListener {
            override fun onItemDelete(audioCardModel: AudioCardModel) {
                initPlayers()
            }
        })

        /**
         * Once you change the one of the audio card's position,
         * such as drag it to up or down or delete it from the track, it will play from the start,
         * so we have to reset the players here
         */
        listDragAdapter2.setOnItemDeleteListener(object : ListDragAdapter.OnItemDeleteListener {
            override fun onItemDelete(audioCardModel: AudioCardModel) {
                initPlayers()
            }
        })

        /**
         * Add the audio card from the audio list page
         * Once you add a new audio card to the track, it will play from the start,
         * so we have to reset the players here
         */
        FragmentLibraryAudioList.setMyAddToTrackListener(object : FragmentLibraryAudioList.addToTrackListener {
            override fun addToTrack(trackNum: Int, audioCardModel: AudioCardModel) {

                if (trackNum == AUDIO_TRACK_ONE) {
                    audioCardsListOnTrack1.add(audioCardModel)
                    listDragAdapter1.notifyDataSetChanged()
                    initPlayers()
                } else {
                    audioCardsListOnTrack2.add(audioCardModel)
                    listDragAdapter2.notifyDataSetChanged()
                    initPlayers()
                }
            }
        })

        /**
         * Add the audio card from the search page
         * Once you add a new audio card to the track, it will play from the start,
         * so we have to reset the players here
         */
        SearchActivity.setMyAddToTrackListener(object : SearchActivity.AddToTrackListener {
            override fun addToTrack(trackNum: Int, audioCardModel: AudioCardModel) {
                if (trackNum == AUDIO_TRACK_ONE) {
                    audioCardsListOnTrack1.add(audioCardModel)
                    listDragAdapter1.notifyDataSetChanged()
                    initPlayers()
                } else {
                    audioCardsListOnTrack2.add(audioCardModel)
                    listDragAdapter2.notifyDataSetChanged()
                    initPlayers()
                }
            }
        })

        playButton.setOnClickListener {

            log_e("isPlayedAlready1 = ${isPlayedAlready1}")
            log_e("isPlayedAlready2 = ${isPlayedAlready2}")
            log_e("playerOnTrack1 = ${playerOnTrack1 == null}")
            log_e("playerOnTrack2 = ${playerOnTrack2 == null}")

            /**
             * If this audio has not been played yet, the play it
             * Here, If it is playing then pause it, otherwise start playing it
             */
            if (!isPlayedAlready1) {
                playerOnTrack1?.let {
                    if (it.isPlaying) {
                        it.pause()
                        isPlayingOnTrack1 = false

                        /**
                         * If we pause the aduio, then we have to make the dynamic flag disappear
                         */
                        animationControl(recyclerView1, false)
                        itemTouchHelperCallback.setDraggable(true)
                        itemTouchHelperCallback2.setDraggable(true)
                    } else {
                        it.start()
                        isPlayingOnTrack1 = true

                        /**
                         * If we start playing the aduio, then we have to make the dynamic flag appear
                         */
                        animationControl(recyclerView1, true)
                        itemTouchHelperCallback.setDraggable(false)
                        itemTouchHelperCallback2.setDraggable(false)
                    }
                }
            }

            /**
             * If this audio has not been played yet, the play it
             * Here, If it is playing then pause it, otherwise start playing it
             */
            if (!isPlayedAlready2) {
                playerOnTrack2?.let {
                    if (it.isPlaying) {
                        it.pause()
                        isPlayingOnTrack2 = false

                        /**
                         * If we pause the aduio, then we have to make the dynamic flag disappear
                         */
                        animationControl(recyclerView2, false)
                        itemTouchHelperCallback.setDraggable(true)
                        itemTouchHelperCallback2.setDraggable(true)
                    } else {
                        it.start()
                        isPlayingOnTrack2 = true

                        /**
                         * If we start playing the aduio, then we have to make the dynamic flag appear
                         */
                        animationControl(recyclerView2, true)
                        itemTouchHelperCallback.setDraggable(false)
                        itemTouchHelperCallback2.setDraggable(false)
                    }
                }
            }

            /**
             * If one of the aduio Tracks is active, then set up the icon as ic_pause
             */
            if (isPlayingOnTrack1 || isPlayingOnTrack2) {
                playButton.setImageResource(R.drawable.ic_pause)
            } else {
                playButton.setImageResource(R.drawable.ic_play_arrow)
            }
        }
    }

    /**
     * To control the visibility of the dynamic flag which stands for which audio is playing
     */
    private fun animationControl(recyclerView: RecyclerView, appear: Boolean) {
        if (appear) {
            recyclerView.layoutManager?.let {
                it.getChildAt(currentIndex)?.let { it ->
                    val holder = recyclerView.getChildViewHolder(it) as ListDragAdapter.ViewHolder
                    holder.lyt_playing.visibility = View.VISIBLE
                    holder.lyt_playing.alpha = 1.0f
                }
            }
        } else {
            recyclerView.layoutManager?.let {
                it.getChildAt(currentIndex)?.let {
                    val holder = recyclerView.getChildViewHolder(it)
                            as ListDragAdapter.ViewHolder
                    holder.lyt_playing.visibility = View.GONE
                    holder.lyt_playing.alpha = 0f
                }
            }
        }

    }

    /**
     * Reset the flags to the default value
     */
    private fun initPlayers() {

        isPlayingOnTrack1 = false
        isPlayingOnTrack2 = false
        isPlayedAlready1 = false
        isPlayedAlready2 = false
        currentIndex = 0

        playerList.clear()
        maxRowCount = Math.max(listDragAdapter1.getItems().size, listDragAdapter2.getItems().size)

        /**
         * playAudioRowByRow() method will return two media players object
         * which will be responsible for respective audi track
         */
        if (!isPlayingOnTrack1 && !isPlayingOnTrack2) {

            playerList = playAudioRowByRow(getAbsolutePath(AUDIO_TRACK_ONE, currentIndex),
                    getAbsolutePath(AUDIO_TRACK_TWO, currentIndex))

            if (playerList.isNotEmpty()) {
                playerOnTrack1 = playerList[0]
            } else {
                playerOnTrack1 = null
            }

            if (playerList.size > 1) {
                playerOnTrack2 = playerList[1]
            } else {
                playerOnTrack2 = null
            }
        }
    }

    private fun playAudioRowByRow(absolutePath1: String?, absolutePath2: String?): ArrayList<MediaPlayer> {

        log_e("Will be playing 01 = $absolutePath1")
        log_e("Will be playing 02 = $absolutePath2")

        val playersList = ArrayList<MediaPlayer>()

        absolutePath1?.let {
            val player1 = MediaPlayer()
            try {
                player1.setDataSource(context, Uri.parse(it))
                player1.prepare();
                player1.setVolume(1f, 1f);
                player1.isLooping = false;

                playersList.add(player1)

                /**
                 * currentIndex != 0 means the current row goes to the second row, In that case, we have to make the
                 * audios on the second row start playing automatically
                 */
                if (currentIndex != 0) {
                    player1.start()
                    isPlayingOnTrack1 = true
                    isPlayedAlready1 = false

                    playerOnTrack1 = player1
                    playButton.setImageResource(R.drawable.ic_pause)
                    animationControl(recyclerView1, true)
                }

                player1.setOnCompletionListener {
                    Tools.log_e("mediaPlayer $absolutePath1 is over, index: ${currentIndex}")

                    animationControl(recyclerView1, false)
                    swapPlayingFlag(AUDIO_TRACK_ONE, false)

                    isPlayedAlready1 = true

                    if (!isPlayingOnTrack1 && !isPlayingOnTrack2) {
                        if (currentIndex < maxRowCount - 1) {
                            currentIndex++
                            playAudioRowByRow(getAbsolutePath(AUDIO_TRACK_ONE, currentIndex),
                                    getAbsolutePath(AUDIO_TRACK_TWO, currentIndex))
                        } else {
                            /**
                             * The audios of each row have been played, and the reset flags is performed here.
                             */
                            initPlayers()
                        }
                    }
                }
            } catch (e: Exception) {
                log_e("e: ${e.toString()}")
            }
        }

        absolutePath2?.let {
            val player2 = MediaPlayer()
            try {
                player2.setDataSource(context, Uri.parse(it))
                player2.prepare();
                player2.setVolume(1f, 1f);
                player2.isLooping = false;

                playersList.add(player2)

                /**
                 * currentIndex != 0 means the current row goes to the second row, In that case, we have to make the
                 * audios on the second row start playing automatically
                 */
                if (currentIndex != 0) {
                    player2.start()
                    isPlayingOnTrack2 = true
                    playButton.setImageResource(R.drawable.ic_pause)

                    playerOnTrack2 = player2
                    isPlayedAlready2 = false

                    animationControl(recyclerView2, true)
                }

                player2.setOnCompletionListener {

                    Tools.log_e("mediaPlayer $absolutePath2 is over, index: ${currentIndex}")

                    animationControl(recyclerView2, false)
                    swapPlayingFlag(AUDIO_TRACK_TWO, false)

                    isPlayedAlready2 = true

                    if (!isPlayingOnTrack1 && !isPlayingOnTrack2) {
                        if (currentIndex < maxRowCount - 1) {
                            currentIndex++
                            playAudioRowByRow(getAbsolutePath(AUDIO_TRACK_ONE, currentIndex),
                                    getAbsolutePath(AUDIO_TRACK_TWO, currentIndex))
                        } else {
                            /**
                             * The audios of each row have been played, and the reset flags is performed here.
                             */
                            initPlayers()
                        }
                    }
                }
            } catch (e: Exception) {
                log_e("e: $e")
            }
        }

        return playersList
    }

    /**
     * To get the audio file path on a specific row
     * For example, If the track one does not have a audio in second row, then return null
     */
    private fun getAbsolutePath(trackNum: Int, currentIndex: Int): String? {

        if (trackNum == AUDIO_TRACK_ONE) {
            return if (currentIndex >= listDragAdapter1.getItems().size) {
                null
            } else {
                listDragAdapter1.getItems()[currentIndex].file.absolutePath
            }
        } else {
            return if (currentIndex >= listDragAdapter2.getItems().size) {
                null
            } else {
                listDragAdapter2.getItems()[currentIndex].file.absolutePath
            }
        }
    }

    /**
     * Swap the status of audio tracks, to detect if the audio track is active
     */
    private fun swapPlayingFlag(trackNum: Int, isPlaying: Boolean) {
        if (trackNum == AUDIO_TRACK_ONE) {
            isPlayingOnTrack1 = isPlaying
        } else {
            isPlayingOnTrack2 = isPlaying
        }

        Tools.log_e("Track one is active:$isPlayingOnTrack1, Track two is active:$isPlayingOnTrack2")
        if (isPlayingOnTrack1 || isPlayingOnTrack2) {
            playButton.setImageResource(R.drawable.ic_pause)
        } else {
            playButton.setImageResource(R.drawable.ic_play_arrow)
        }
    }

    /**
     * stop player when destroy
     */
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer1.release()
        mediaPlayer2.release()
    }
}
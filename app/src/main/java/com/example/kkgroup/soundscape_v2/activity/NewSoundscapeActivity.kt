package com.example.kkgroup.soundscape_v2.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.widget.MyLinearLayout
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.VerticalRangeSeekBar
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class NewSoundscapeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var seekBar: VerticalRangeSeekBar
    private lateinit var audioTrack01: MyLinearLayout
    private lateinit var audioTrack02: MyLinearLayout
    private lateinit var audioTrack03: MyLinearLayout
    private var buttonsArr: MutableList<TextView> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_soundscape)

        initToolbar()
        initComponents()
        initListeners()

        addButtonView()
    }

    private fun addButtonView() {

        val childView1 = LayoutInflater.from(this)
                .inflate(R.layout.audio_file_small_item, audioTrack01, false)
        val textView1 = childView1.findViewById<TextView>(R.id.text_view)
        textView1.setId(View.generateViewId())

        textView1.text = "第 " + 1 + " 个view"
        initAnimation(textView1, 1)
        audioTrack01.addView(childView1)

        val childView2 = LayoutInflater.from(this)
                .inflate(R.layout.audio_file_small_item, audioTrack02, false)
        val textView2 = childView2.findViewById<TextView>(R.id.text_view)
        textView2.setId(View.generateViewId())

        textView2.text = "第 " + 2 + " 个view"
        initAnimation(textView2, 2)
        audioTrack02.addView(childView2)

        val childView3 = LayoutInflater.from(this)
                .inflate(R.layout.audio_file_small_item, audioTrack03, false)
        val textView3 = childView3.findViewById<TextView>(R.id.text_view)
        textView3.setId(View.generateViewId())

        textView3.text = "第 " + 3 + " 个view"
        initAnimation(textView3, 1)
        audioTrack03.addView(childView3)
        val childView4 = LayoutInflater.from(this)
                .inflate(R.layout.audio_file_small_item, audioTrack01, false)
        val textView4 = childView4.findViewById<TextView>(R.id.text_view)
        textView4.setId(View.generateViewId())

        textView4.text = "第 " + 4 + " 个view"
        initAnimation(textView4, 1)
        audioTrack01.addView(childView4)
//        val button1 = Button(this);
//        button1.text = "test"
//        button1.width = 80
//        button1.height = 80
//        button1.id = View.generateViewId()
//        button1.setBackgroundResource(R.drawable.btn_rounded_orange_outline)
//        button1.setTextColor(Color.WHITE)
//        button1.setOnClickListener(this)
//        audioTrack01.addView(button1)
//
//        val button2 = Button(this);
//        button2.text = "test"
//        button2.width = 80
//        button2.height = 80
//        button2.id = View.generateViewId()
//        button2.setBackgroundResource(R.drawable.btn_rounded_orange_outline)
//        button2.setTextColor(Color.WHITE)
//        button2.setOnClickListener(this)
//        audioTrack01.addView(button2)
//
//        val button3 = Button(this);
//        button3.text = "test"
//        button3.width = 80
//        button3.height = 80
//        button3.id = View.generateViewId()
//        button3.setBackgroundResource(R.drawable.btn_rounded_orange_outline)
//        button3.setTextColor(Color.WHITE)
//        button3.setOnClickListener(this)
//        audioTrack02.addView(button3)
//
//        val button4 = Button(this);
//        button4.text = "test"
//        button4.width = 80
//        button4.height = 80
//        button4.id = View.generateViewId()
//        button4.setBackgroundResource(R.drawable.btn_rounded_orange_outline)
//        button4.setTextColor(Color.WHITE)
//        button4.setOnClickListener(this)
//        audioTrack03.addView(button4)
//
        buttonsArr.add(textView1)
        buttonsArr.add(textView2)
        buttonsArr.add(textView3)
    }

    private fun initAnimation(textView: TextView, position: Int) {
        when (position) {
            1 -> {
                val mLeftAnimation1 = TranslateAnimation(Animation.RELATIVE_TO_SELF, -1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                mLeftAnimation1.duration = 500;
                textView.startAnimation(mLeftAnimation1);
                textView.animate().alpha(1f);
            }

            2 -> {
                val mLeftAnimation2 = TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                mLeftAnimation2.setDuration(500);
                textView.startAnimation(mLeftAnimation2);
                textView.animate().alpha(1f);
            }
        }

    }

    override fun onClick(view: View) {
        Tools.toastShow(this, view?.id.toString())


        for (temp in buttonsArr) {
            if (view.id == temp.id) {
                Tools.toastShow(this, temp.text.toString())
            }
        }
    }

    private fun initComponents() {
        seekBar = findViewById(R.id.myVerticalSeekbar)
        seekBar.setValue(30f)

        // make it work!
        seekBar.invalidate()

        audioTrack01 = findViewById(R.id.audio_track_one)
        audioTrack02 = findViewById(R.id.audio_track_two)
        audioTrack03 = findViewById(R.id.audio_track_three)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Soundscape V2"
        Tools.setSystemBarColor(this, R.color.colorPrimary)
    }

    private fun initListeners() {

        seekBar.setOnRangeChangedListener(object : OnRangeChangedListener {
            override fun onStartTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

            override fun onRangeChanged(view: RangeSeekBar?, leftValue: Float, rightValue: Float, isFromUser: Boolean) {

                Tools.log_e("leftValue: $leftValue --> rightValue: $rightValue")
            }

            override fun onStopTrackingTouch(view: RangeSeekBar?, isLeft: Boolean) {

            }

        })

        // make it work!
        seekBar.invalidate()
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


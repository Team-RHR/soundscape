package com.example.kkgroup.soundscape_v2.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.widget.MyLinearLayout
import com.jaygoo.widget.OnRangeChangedListener
import com.jaygoo.widget.RangeSeekBar
import com.jaygoo.widget.VerticalRangeSeekBar
import org.jetbrains.anko.startActivity

class NewSoundscapeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var seekBar: VerticalRangeSeekBar
    private lateinit var audioTrack01: MyLinearLayout
    private lateinit var audioTrack02: MyLinearLayout
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
//        val textView1 = childView1.findViewById<TextView>(R.id.text_view)
//        textView1.setId(View.generateViewId())
//
//        textView1.text = "第 " + 1 + " 个view"
//        initAnimation(textView1, 1)
        audioTrack01.addView(childView1)

        val childView2 = LayoutInflater.from(this)
                .inflate(R.layout.audio_file_small_item, audioTrack02, false)

//        val textView1 = childView1.findViewById<TextView>(R.id.text_view)
//        textView1.setId(View.generateViewId())
//
//        textView1.text = "第 " + 1 + " 个view"
//        initAnimation(textView1, 1)
        audioTrack02.addView(childView2)

        childView2.setOnClickListener {
            Toast.makeText(this, "cliclk childView2", Toast.LENGTH_SHORT).show()
        }

        val childView3 = LayoutInflater.from(this)
                .inflate(R.layout.audio_file_small_item, audioTrack02, false)
//        val textView1 = childView1.findViewById<TextView>(R.id.text_view)
//        textView1.setId(View.generateViewId())
//
//        textView1.text = "第 " + 1 + " 个view"
//        initAnimation(textView1, 1)
        audioTrack02.addView(childView3)

        childView3.setOnClickListener {
            Toast.makeText(this, "cliclk childView3", Toast.LENGTH_SHORT).show()
        }

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


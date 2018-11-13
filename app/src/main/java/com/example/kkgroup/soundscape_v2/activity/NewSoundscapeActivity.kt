package com.example.kkgroup.soundscape_v2.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import kotlinx.android.synthetic.main.activity_new_soundscape.*
import org.jetbrains.anko.startActivity

class NewSoundscapeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_soundscape)

        initToolbar()
        initListeners()
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Soundscape V2"
        Tools.setSystemBarColor(this, R.color.colorPrimary)
    }

    private fun initListeners() {
        fabInNewSoundscapePage.setOnClickListener { startActivity<SelectAudioActivity>() }
    }
}

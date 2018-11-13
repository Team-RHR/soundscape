package com.example.kkgroup.soundscape_v2.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.fragments.AudioListFragment
import com.example.kkgroup.soundscape_v2.fragments.RecordingFragment
import kotlinx.android.synthetic.main.select_audio_file.*

class SelectAudioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.select_audio_file)

        val adapter = myViewPagerAdapter (supportFragmentManager)
        adapter.addFragment(AudioListFragment(), "Select")
        adapter.addFragment(RecordingFragment(), "Record")
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)

    }




      class myViewPagerAdapter (manager: FragmentManager) : FragmentPagerAdapter (manager) {

          private val fragmentList : MutableList<Fragment> = ArrayList()
          private val titleList : MutableList<String> = ArrayList()

          override fun getItem(p0: Int): Fragment {
            return fragmentList[p0]
          }

          override fun getCount(): Int {
              return fragmentList.size
          }

          fun addFragment (fragment: Fragment, title:String) {
              fragmentList.add(fragment)
              titleList.add(title)
          }

          override fun getPageTitle(position: Int): CharSequence? {
              return titleList[position]
          }

      }
    }






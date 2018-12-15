package com.example.kkgroup.soundscape_v2.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.ArrayList

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 2:32 2018/12/5
 * @ Description：Exclusive adapter for viewPager to inflate 4 fragments
 */
class MyViewPagerAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun getTitle(position: Int): String {
        return mFragmentTitleList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}
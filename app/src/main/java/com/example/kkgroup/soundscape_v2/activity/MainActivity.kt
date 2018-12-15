package com.example.kkgroup.soundscape_v2.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.MyViewPagerAdapter
import com.example.kkgroup.soundscape_v2.fragment.FragmentNewSoundscape
import com.example.kkgroup.soundscape_v2.fragment.FragmentPreference
import com.example.kkgroup.soundscape_v2.fragment.FragmentRecording
import com.example.kkgroup.soundscape_v2.fragment.FragmentRoot
import org.jetbrains.anko.startActivity
import kotlin.system.exitProcess

/**
 * description: This Activity is the main page which includes 4 fragments inside it,
 *              Basically, this activity will do nothing, but loads 4 fragments as a container
 * create time: 13:39 2018/12/15
 */
private const val REQUEST_WRITE_EXTERNAL_PERMISSION = 200
class MainActivity : AppCompatActivity() {

    private var mExitTime: Long = 0
    private lateinit var view_pager: ViewPager
    private lateinit var tab_layout: TabLayout
    private lateinit var viewPagerAdapter: MyViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermission()
        initToolbar()
        initComponents()
        initListeners()
    }

    /**
     * Load our customized Toolbar which contains a search item
     * And the default fragment is the Recording Fragment
     */
    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_logo_sized)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Recording"
    }

    /**
     * setup viewPager
     */
    private fun initComponents() {

        view_pager = findViewById(R.id.view_pager)
        tab_layout = findViewById(R.id.tab_layout)

        setupViewPager(view_pager)
        tab_layout.setupWithViewPager(view_pager)

        // set icon for each tab
        tab_layout.getTabAt(0)!!.setIcon(R.drawable.ic_mic)
        tab_layout.getTabAt(1)!!.setIcon(R.drawable.ic_cloud_download)
        tab_layout.getTabAt(2)!!.setIcon(R.drawable.ic_queue_music)
        tab_layout.getTabAt(3)!!.setIcon(R.drawable.ic_settings_white_24dp)

        // set icon color pre-selected
        tab_layout.getTabAt(0)!!.icon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        tab_layout.getTabAt(1)!!.icon!!.setColorFilter(resources.getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN)
        tab_layout.getTabAt(2)!!.icon!!.setColorFilter(resources.getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN)
        tab_layout.getTabAt(3)!!.icon!!.setColorFilter(resources.getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN)
    }

    /**
     * setup UI event listener
     */
    private fun initListeners() {

        /**
         * update tab icon's color when tab selected
         */
        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                supportActionBar!!.title = viewPagerAdapter.getTitle(tab.position)
                tab.icon!!.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.icon!!.setColorFilter(resources.getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
    }

    /**
     * Request permission for write storage
     */
    private fun requestPermission() {
        if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            /**
             * No permission
             */
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ,REQUEST_WRITE_EXTERNAL_PERMISSION)
        }else{
            /**
             * have permission
             */
            Tools.updateAudioFiles()
        }
    }

    /**
     * Add 4 fragments to viewPager
     */
    private fun setupViewPager(viewPager: ViewPager) {
        viewPagerAdapter = MyViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(FragmentRecording.newInstance(), getString(R.string.recoring))
        viewPagerAdapter.addFragment(FragmentRoot.newInstance(), getString(R.string.Library))
        viewPagerAdapter.addFragment(FragmentNewSoundscape.newInstance(), getString(R.string.new_soundscape))
        viewPagerAdapter.addFragment(FragmentPreference.newInstance(), getString(R.string.preference))
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = viewPagerAdapter
    }

    /**
     * inflate our toolbar xml
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    /**
     * set up toolbar's item click event
     * if click search icon, then go to the search activity
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search){
            startActivity<SearchActivity>()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * When back button on the phone is pressed
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - mExitTime > 2000) {
                Tools.toastShow(this, getString(R.string.toast_press_again_exit))
                mExitTime = System.currentTimeMillis()
            } else {
                Tools.toastCancel()
                moveTaskToBack(true)
                exitProcess(1)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_WRITE_EXTERNAL_PERMISSION -> {
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Tools.toastShow(this, getString(R.string.toast_permission_denied))
                    finish()
                }
                return
            }
        }
    }
}

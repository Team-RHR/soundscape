package com.example.kkgroup.soundscape_v2.Tools

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.support.annotation.ColorRes
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.R
import java.io.File
import java.lang.reflect.Array

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 21:22 2018/11/6
 * @ Description：Build for Metropolia project
 */
object Tools {

    // In this project, we use the same TAG for debugging
    private val TAG = "hero"
    private val audioFormat = ".3gp"
    private var isShow = true
    private var toast: Toast? = null

    // return -> /storage/emulated/0/soundscape/
    fun getSoundScapePath(): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + ConstantValue.destFolderStr + File.separator
    }

    // always use Log.e Not Log.d
    fun log_e(message: String) {
        Log.e(TAG, message)
    }

    // cutomized Toast style
    fun toastShow(context: Context, message: String?) {
        if (isShow) {
            toast = Toast(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.mtoast_layout, null)
            val tv = view.findViewById(R.id.toast_tv) as TextView
            if (message != null) {
                tv.text = message
            }
            toast!!.view = view
            toast!!.duration = Toast.LENGTH_SHORT
            toast!!.show()
        }
    }

    fun toastCancel() {
        toast?.cancel()
    }

    // setup system title bar color
    fun setSystemBarColor(act: Activity, @ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = act.resources.getColor(color)
        }
    }

    // view fade out effect, Prepare the View for the animation
    fun viewFadeOut(v: View) {
        v.alpha = 1.0f
        v.animate().setDuration(500).alpha(0.0f)
    }

    // return list of audio file in a certain folder
    fun getLocalAudioFiles(folderPath: String) : MutableList<File>{

        val folderPath = File(folderPath)
        if (!folderPath.exists()) folderPath.mkdirs()

        val listFiles = folderPath.listFiles().filter {
            it.name.endsWith(Tools.audioFormat)
        }
        return listFiles.toMutableList()
    }

    /**
     * collapse the view, used in search suggestion bar
     */
    fun viewCollapse(v: View) {
        val a = collapseAction(v)
        v.startAnimation(a)
    }

    fun viewExpand(v: View) {
        val a = expandAction(v)
        v.startAnimation(a)
    }

    /**
     * Cutomized Animation, learned from online blog
     */
    private fun expandAction(v: View): Animation {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val targtetHeight = v.measuredHeight
        v.layoutParams.height = 0
        v.visibility = View.VISIBLE
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targtetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        a.duration = (targtetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
        return a
    }
    private fun collapseAction(v: View): Animation {
        val initialHeight = v.measuredHeight
        val a = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
        return a
    }
}
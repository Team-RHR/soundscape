package com.example.kkgroup.soundscape_v2.Tools

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Environment
import android.support.annotation.ColorRes
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.TextView
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.Model.SearchApiModel
import com.example.kkgroup.soundscape_v2.R
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import okhttp3.*
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 21:22 2018/11/6
 * @ Description：A bunch of handy static methods, Please see instruction of each method
 */
object Tools {

    /**
     *  In this project, we use the same TAG for debugging
     */
    private val TAG = "hero"
    private val audioFormat = ".mp3"
    private var isShow = true
    private var toast: Toast? = null

    /**
     * Folder Structure:
     *                                       1 --- Human
     *                 1 --- downloads  -->  2 --- Machine
     *                                       3 --- Nature
     *                                       4 --- temp (for search result)
     * soundscape -->  2 --- myRecording
     *
     *                 3 --- mySoundscape
     */

    // return -> /storage/emulated/0/soundscape/
    private fun getRootPath(): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + ConstantValue.destFolderStr + File.separator
    }

    // return -> /storage/emulated/0/soundscape/downloads
    private fun getDownloadPath(): String {
        return getRootPath() + "downloads" + File.separator
    }

    // return -> /storage/emulated/0/soundscape/downloads/Human
    fun getDownloadedAudioByCategoryPath(categoryName: String): String {
        return getDownloadPath() + categoryName + File.separator
    }

    // return -> /storage/emulated/0/soundscape/mySoundscape
    fun getMySoundscapePath(): String {
        return getRootPath() + "mySoundscape" + File.separator
    }

    // return -> /storage/emulated/0/soundscape/myRecording
    fun getMyRecordingPath(): String {
        return getRootPath() + "myRecording" + File.separator
    }

    // always use Log.e Not Log.d
    fun log_e(message: String) {
        Log.e(TAG, message)
    }

    /**
     * cutomized Toast style
     */
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

    /**
     * setup system title bar color
     */
    fun setSystemBarColor(act: Activity, @ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = act.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = act.resources.getColor(color)
        }
    }

    /**
     * Convert dp to px
     */
    fun dpToPx(ctx: Context, dp: Int): Int {
        val r = ctx.resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    /**
     * view fade out effect, Prepare the View for the animation
     */
    fun viewFadeOut(v: View) {
        v.alpha = 1.0f
        v.animate().setDuration(500).alpha(0.0f)
    }

    /**
     * return list of audio file in a certain folder,
     * It will return a list of audio files whcih file name ends with .3gp or .mp3
     */
    fun getLocalAudioFiles(folderPath: String): MutableList<File> {
        val folderPath = File(folderPath)
        if (!folderPath.exists()) {
            val mkdirs = folderPath.mkdirs()
        }

        val listFiles = folderPath.listFiles().filter {
            it.name.endsWith(".3gp") || it.name.endsWith(Tools.audioFormat)
        }
        return listFiles.toMutableList()
    }

    /**
     * return list of audio file in a certain folder,
     * It will return a list of audio files whcih file name ends with .3gp or .mp3
     */
    fun getRemoteAudioFiles(folderPath: String): MutableList<File> {

        val folderPath = File(folderPath)
        if (!folderPath.exists()) folderPath.mkdirs()

        val listFiles = folderPath.listFiles().filter {
            it.name.endsWith(Tools.audioFormat)
        }
        return listFiles.toMutableList()
    }

    /**
     * return list of audio file in a certain folder,
     * It will return a list of audio files whcih file name ends with .3gp or .mp3
     */
    fun getMyRecordingsFiles(folderPath: String): MutableList<File> {

        val folderPath = File(folderPath)
        if (!folderPath.exists()) folderPath.mkdirs()

        val listFiles = folderPath.listFiles().filter {
            it.name.endsWith(".3gp") || it.name.endsWith(Tools.audioFormat)
        }
        return listFiles.toMutableList()
    }

    /**
     * collapse the view, used in search history list in search page
     */
    fun viewCollapse(v: View) {
        val a = collapseAction(v)
        v.startAnimation(a)
    }

    /**
     * expand the view, used in search history list in search page
     */
    fun viewExpand(v: View) {
        val a = expandAction(v)
        v.startAnimation(a)
    }

    /**
     * Animation of how to expand a view
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

    /**
     * Animation of how to collaps a view
     * Cutomized Animation, learned from online blog
     */
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

    /**
     * When entering the homepage, try to download all audios from the backend
     * Whenever there is a network connection, it will automatically check if new audio appears on the backend.
     * If yes, then downloading this new audio, other wise it will not do anything
     */
    fun updateAudioFiles() {

        val directoryNature = File(Tools.getDownloadPath(), "Nature")
        val directoryHuman = File(Tools.getDownloadPath(), "Human")
        val directoryMachine = File(Tools.getDownloadPath(), "Machine")
        directoryNature.mkdirs()
        directoryHuman.mkdirs()
        directoryMachine.mkdirs()

        val call = Networking.service.getAllMp3FilesWithLink(Networking.API_TOKEN, "true", "mp3")
        val value = object : retrofit2.Callback<JsonArray> {
            override fun onResponse(call: retrofit2.Call<JsonArray>,
                                    response: retrofit2.Response<JsonArray>?) {
                response?.let {
                    if (response.isSuccessful) {
                        val res = "[" + response.body().toString().filter { c: Char -> (c.toString() != "[" && c.toString() != "]") } + "]"
                        val gson = GsonBuilder()
                                .setLenient()       // fix parse json failed on android 6.0 by setLenient()
                                .create()
                        val model: kotlin.Array<SearchApiModel> = gson.fromJson(res, kotlin.Array<SearchApiModel>::class.java)

                        for (temp in model) {
                            if (!isAlreadyDownloaded(temp)) {
                                downloadAudio(temp)
                            }
                        }
                    }
                }
            }

            /**
             * this method gets called if the http call fails (no internet etc)
             */
            override fun onFailure(call: retrofit2.Call<JsonArray>, t: Throwable) {
                Tools.log_e("${t.message}")
            }
        }
        call.enqueue(value)
    }

    /**
     * check if the audio has been downloaded by checking the file's name
     */
    fun isAlreadyDownloaded(obj: SearchApiModel): Boolean {
        var folderName = "temp"
        if (obj.category != null) {
            when (obj.category) {
                "human" -> {
                    folderName = "Human"
                }

                "machine" -> {
                    folderName = "Machine"
                }

                "nature" -> {
                    folderName = "Nature"
                }
            }
        }
        return (File(Tools.getDownloadedAudioByCategoryPath(folderName) + obj.title + Tools.audioFormat)
                .exists())
    }

    /**
     * downloadd audio ins seach page
     */
    fun downloadAudio(obj: SearchApiModel) {

        var downloadDest = Tools.getDownloadedAudioByCategoryPath("Temp")
        when (obj.category) {
            "human" -> {
                downloadDest = Tools.getDownloadedAudioByCategoryPath("Human")
            }

            "machine" -> {
                downloadDest = Tools.getDownloadedAudioByCategoryPath("Machine")
            }

            "nature" -> {
                downloadDest = Tools.getDownloadedAudioByCategoryPath("Nature")
            }
        }

        val okClient by lazy {
            OkHttpClient()
        }
        val okRequest by lazy {
            Request.Builder()
                    .url(obj.downloadLink)
                    .build()
        }

        okClient.newCall(okRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val inputStream = response.body()?.byteStream()

                if (inputStream != null) {
                    File(downloadDest, "${obj.title}.mp3").copyInputStreamToFile(inputStream)
                    Tools.log_e("File: ${obj.title} written successfully!")
                }
            }
        })
    }

    /**
     * Get audio path, return null means this audio does not exist
     */
    fun getAudioPathByObj(obj: SearchApiModel): String? {
        var folderName = "Temp"
        when (obj.category) {
            "human" -> {
                folderName = "Human"
            }

            "machine" -> {
                folderName = "Machine"
            }

            "nature" -> {
                folderName = "Nature"
            }
        }
        return if (isAlreadyDownloaded(obj)) {
            Tools.getDownloadedAudioByCategoryPath(folderName) + obj.title + Tools.audioFormat
        } else {
            null
        }
    }

    /**
     * Write file to device
     */
    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        inputStream.use { input ->
            this.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    /**
     * delete Audios
     */
    fun deleteAudios() {
        val folderDir = File(getDownloadPath())
        folderDir.deleteRecursively()
        folderDir.mkdirs()
    }

    /**
     * delete SoundScapes
     */
    fun deleteSoundScapes() {
        val folderDir = File(getMySoundscapePath())
        folderDir.deleteRecursively()
        folderDir.mkdirs()
    }

    /**
     * delete my Recordings
     */
    fun deleteRecordings() {
        val folderDir = File(getMyRecordingPath())
        folderDir.deleteRecursively()
        folderDir.mkdirs()
    }

}
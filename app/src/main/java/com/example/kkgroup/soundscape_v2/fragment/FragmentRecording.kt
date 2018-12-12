package com.example.kkgroup.soundscape_v2.fragment


import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import kotlinx.android.synthetic.main.fragment_recording.*
import java.io.File
import java.io.IOException
import java.util.*

private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class FragmentRecording : Fragment() {
    private var permissionToRecordAccepted = false
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var audioFile: File? = null
    private var mStartRecording = true
    private var fileName = ""


    // ------------ Timer code ------------

    inner class MyCounter(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            textView_Countdown.text = "00:00"
            fab_start.setImageDrawable(resources.getDrawable(R.drawable.ic_mic))
        }

        override fun onTick(millisUntilFinished: Long) {
            var second = 30 - (millisUntilFinished / 1000)
            if (second < 10) textView_Countdown.text = "00:0" + second.toString() + "" else textView_Countdown.text = "00:" + second.toString() + ""
        }
    }

    // ------------ Timer code end ------------

    companion object {
        fun newInstance(): FragmentRecording {
            return FragmentRecording()
        }
    }

    private var root: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_recording, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS), REQUEST_RECORD_AUDIO_PERMISSION)
        }

        val timer = MyCounter(30000, 1000)
        //fab_pause.setOnClickListener{ timer.cancel() }
        initListeners(timer)

        /* recordingBtn.setOnClickListener {
             onRecord(mStartRecording)
             if (mStartRecording) {
                 recordingBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_stop))
             } else {
                 recordingBtn.setImageDrawable(resources.getDrawable(R.drawable.ic_mic))
             }

             mStartRecording = !mStartRecording
         }

         pauseButton.setOnClickListener() {
         pauseRecording()
         }

         unPauseButton.setOnClickListener() {
         unPauseRecording()
         }
         */
    }

    private fun initListeners(timer: MyCounter) {
        fab_start.setOnClickListener {
            onRecord(mStartRecording)
            if (mStartRecording) {
                fab_start.setImageDrawable(resources.getDrawable(R.drawable.ic_stop))
                timer.start()
            } else {
                fab_start.setImageDrawable(resources.getDrawable(R.drawable.ic_mic))
                timer.cancel()
                textView_Countdown.text = getString(R.string.time00)
                showLogOutDialog()
            }
            mStartRecording = !mStartRecording
        }

    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        stopRecording()
    }

    private fun startRecording() {
        fileName = "${Date().time}.mp3"
        audioFile = File(Tools.getMyRecordingPath() + fileName)

        mRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(8)
            setAudioSamplingRate(44100)
            setOutputFile(audioFile?.absolutePath)
            try {
                prepare()
            } catch (e: IOException) {
                Tools.log_e("prepare() failed")
            }
            start()
        }
    }

    private fun stopRecording() {
        mRecorder?.apply {
            stop()
            release()
        }
        mRecorder = null
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            Tools.toastShow(context!!, getString(R.string.toast_permission_denied))
        }
    }

    override fun onStop() {
        super.onStop()
        mRecorder?.release()
        mRecorder = null
        mPlayer?.release()
        mPlayer = null
    }


    // Dialog for saving mp3
    private fun showLogOutDialog() {

        val dialog1 = Dialog(this.context)
        dialog1.setContentView((R.layout.save_audio_dialog))
        val inputField = dialog1.findViewById<TextView>(R.id.filename_field)
        inputField.hint = this.fileName

        val deleteButton = dialog1.findViewById<TextView>(R.id.deleteButton)
        val saveButton = dialog1.findViewById<TextView>(R.id.saveButton)
        val fileDir = File(Tools.getMyRecordingPath() + File.separator + fileName)

        deleteButton.setOnClickListener {
            fileDir.delete()
            dialog1.dismiss()
            Tools.toastShow(root!!.context, getString(R.string.record_delete))
        }

        saveButton.setOnClickListener {
            val customName = inputField.text.toString()

            if (customName != "") {
                fileDir.renameTo(File(Tools.getMyRecordingPath() + File.separator + customName + ".mp3"))
            }
            dialog1.dismiss()
            Tools.toastShow(root!!.context, getString(R.string.record_save))
        }

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog1.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT


        dialog1.show()
        dialog1.window!!.attributes = lp

    }

}

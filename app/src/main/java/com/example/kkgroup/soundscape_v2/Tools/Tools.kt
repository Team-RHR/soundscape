package com.example.kkgroup.soundscape_v2.Tools

import android.os.Environment
import java.io.File

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 21:22 2018/11/6
 * @ Description：Build for Metropolia project
 */
class Tools {

    companion object {

        // return -> /storage/emulated/0/soundscape/
        fun getSoundScapePath(): String{
            return Environment.getExternalStorageDirectory().absolutePath + File.separator + "soundscape" + File.separator
        }
    }

}
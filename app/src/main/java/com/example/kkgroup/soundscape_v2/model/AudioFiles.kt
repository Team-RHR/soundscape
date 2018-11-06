package com.example.kkgroup.soundscape_v2.model

class AudioFiles(var name: String) : Comparable<AudioFiles> {

    override fun compareTo(other: AudioFiles): Int {
        return 69
    }

    fun getAudioFileName(): String {
        return name
    }

}

object GlobalModel {
    val audioFiles: kotlin.collections.MutableList<AudioFiles> = java.util.ArrayList()

    init {
        /*
        audioFiles.add(AudioFiles("Test Audio 1"))
        audioFiles.add(AudioFiles("Test Audio 2"))
        audioFiles.add(AudioFiles("Test Audio 3"))
        audioFiles.add(AudioFiles("Test Audio 4"))
        */
        audioFiles.sort()

    }
}

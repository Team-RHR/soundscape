package com.example.kkgroup.soundscape_v2.Model

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 22:56 2018/12/2
 * @ Description：Build for Metropolia project
 */
data class AudioCardModel(
        val category: String = "Human",
        val name: String = "Here is the audio name",
        val duration: String = "3:45",
        var volume: String = "88/100",
        val trackNum: Int,
        var leftPosition: Int,
        var topPosition: Int,
        var rightPosition: Int,
        var bottomPosition: Int,
        var isDraggable: Boolean)
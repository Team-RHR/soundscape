package com.example.kkgroup.soundscape_v2.Model

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 22:56 2018/12/2
 * @ Description：Build for Metropolia project
 */
data class AudioCardModel(
        val trackNum: Int,
        var topPosition: Int,
        var bottomPosition: Int,
        var isDraggable: Boolean)
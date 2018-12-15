package com.example.kkgroup.soundscape_v2.Model

import java.io.File

/**
 * @ Author     ：Hao Zhang.
 * @ Date       ：Created in 22:56 2018/12/2
 * @ Description：Model for audio cards in new soundscape page
 */
data class AudioCardModel(
        val category: String,
        val file: File,
        val bgColor: Int)
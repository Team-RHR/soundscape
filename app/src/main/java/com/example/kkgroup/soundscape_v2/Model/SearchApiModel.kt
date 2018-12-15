package com.example.kkgroup.soundscape_v2.Model;

import com.google.gson.annotations.SerializedName

/**
 * description: Model for encapsulate the search result data
 * create time: 15:09 2018/12/15
 */
data class SearchApiModel(@SerializedName("Title") val title: String,
                          @SerializedName("Original filename") val originalFilename: String,
                          @SerializedName("Description") val description: String,
                          @SerializedName("Category") val category: String? = null,
                          @SerializedName("Sound Type") val soundType: String,
                          @SerializedName("Length (sec)") val lengthSec: String,
                          @SerializedName("Creation date") val creationDate: String,
                          @SerializedName("File extension") val fileExtension: String,
                          @SerializedName("File size(KB)") val fileSizeKB: String,
                          @SerializedName("Created by") val createdBy: String,
                          @SerializedName("Collection name") val collectionName: String,
                          @SerializedName("Collection ID") val collectionID: String,
                          @SerializedName("Download link") val downloadLink: String?)
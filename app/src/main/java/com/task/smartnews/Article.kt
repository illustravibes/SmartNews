package com.task.smartnews

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: Int,
    val title: String,
    val content: String,
    val imageBlob: ByteArray?
) : Parcelable

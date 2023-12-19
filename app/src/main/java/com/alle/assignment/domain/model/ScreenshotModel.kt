package com.alle.assignment.domain.model

import android.net.Uri

data class ScreenshotModel(
    val id: Long,
    val fileName: String,
    val fileUri: Uri
)
package com.alle.assignment.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface TextRecognizerRepository {

    suspend fun getOCRText(imageUri: Uri): Flow<Resource<String>>

}
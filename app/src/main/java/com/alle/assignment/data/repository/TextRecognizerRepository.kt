package com.alle.assignment.data.repository

import android.net.Uri
import com.alle.assignment.domain.model.ImageEntity
import kotlinx.coroutines.flow.Flow

interface TextRecognizerRepository {

    suspend fun getOCRText(imageUri: Uri): Flow<Resource<String>>
    suspend fun getImageLabel(imageUri: Uri): Flow<Resource<List<String>>>
    suspend fun getLocalImageEntity(imageName: String): ImageEntity?
    suspend fun setInactiveCollection(
        imageName: String, removedCollection: String
    ): Flow<Resource<ImageEntity>>
    suspend fun setActiveCollection(
        imageName: String, removedCollection: String
    ): Flow<Resource<ImageEntity>>
}
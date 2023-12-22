package com.alle.assignment.domain.repository

import android.content.Context
import android.net.Uri
import com.alle.assignment.data.local.ImageDao
import com.alle.assignment.data.local.ImageDatabase
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.data.repository.TextRecognizerRepository
import com.alle.assignment.domain.model.ImageCollections
import com.alle.assignment.domain.model.ImageDescription
import com.alle.assignment.domain.model.ImageEntity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

class TextRecognizerRepositoryImpl @Inject constructor(
    private val textRecognizer: TextRecognizer,
    private val imageLabeler: ImageLabeler,
    private val imageDao: ImageDao,
    @ApplicationContext private val appContext: Context
) : TextRecognizerRepository {
    override suspend fun getOCRText(imageUri: Uri): Flow<Resource<String>> {
        return callbackFlow {
            try {
                val image = InputImage.fromFilePath(appContext, imageUri)
                trySend(Resource.Loading())
                textRecognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        // Task completed successfully
                        val description = visionText.text
                        trySend(Resource.Success(description))
                        CoroutineScope(Dispatchers.IO).launch {
                            imageDao.upsertImageDescription(
                                ImageDescription(
                                    imageUri.toString(), description
                                )
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        trySend(Resource.Failed(e.message ?: e.toString()))
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                trySend(Resource.Failed(e.message ?: e.toString()))
            }
            awaitClose()
        }
    }

    override suspend fun getImageLabel(imageUri: Uri): Flow<Resource<List<String>>> {
        return callbackFlow {
            try {
                val image = InputImage.fromFilePath(appContext, imageUri)
                trySend(Resource.Loading())
                imageLabeler.process(image)
                    .addOnSuccessListener { imageLabels ->
                        val collections = imageLabels.map { it.text }
                        trySend(Resource.Success(collections))
                        CoroutineScope(Dispatchers.IO).launch {
                            imageDao.upsertImageCollection(
                                ImageCollections(
                                    imageUri.toString(), collections
                                )
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        trySend(Resource.Failed(e.message ?: e.toString()))
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                trySend(Resource.Failed(e.message ?: e.toString()))
            }
            awaitClose()
        }
    }

    override suspend fun getLocalImageEntity(imageName: String): ImageEntity? {
        return imageDao.getImageModelByImageName(imageName)
    }

    override suspend fun setInactiveCollection(
        imageName: String, removedCollection: String
    ): Flow<Resource<ImageEntity>> {
        return flow {
            imageDao.getImageModelByImageName(imageName)?.let {
                val collectionList = ArrayList(it.collections)
                val inactiveCollectionList = ArrayList(it.inactiveCollections)
                if (collectionList.remove(removedCollection)) {
                    it.collections = collectionList
                    inactiveCollectionList.add(removedCollection)
                    it.inactiveCollections = inactiveCollectionList
                    imageDao.updateImageEntity(it)
                }
                emit(Resource.Success(it))
            } ?: run {
                emit(Resource.Failed("Image Entity Not Found"))
            }
        }
    }

    override suspend fun setActiveCollection(
        imageName: String,
        removedCollection: String
    ): Flow<Resource<ImageEntity>> {
        return flow {
            imageDao.getImageModelByImageName(imageName)?.let {
                val collectionList = ArrayList(it.collections)
                val inactiveCollectionList = ArrayList(it.inactiveCollections)
                if (inactiveCollectionList.remove(removedCollection)) {
                    it.inactiveCollections = inactiveCollectionList
                    collectionList.add(removedCollection)
                    it.collections = inactiveCollectionList
                    imageDao.updateImageEntity(it)
                }
                emit(Resource.Success(it))
            } ?: run {
                emit(Resource.Failed("Image Entity Not Found"))
            }
        }
    }
}
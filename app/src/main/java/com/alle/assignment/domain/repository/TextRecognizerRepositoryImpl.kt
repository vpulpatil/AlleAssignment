package com.alle.assignment.domain.repository

import android.content.Context
import android.net.Uri
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.data.repository.TextRecognizerRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class TextRecognizerRepositoryImpl @Inject constructor(
    private val textRecognizer: TextRecognizer,
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
                        trySend(Resource.Success(visionText.text))
                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        trySend(Resource.Failed(e.message ?: e.toString()))
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                trySend(Resource.Failed(e.message ?: e.toString()))
            }
            close()
            awaitCancellation()
        }
    }
}
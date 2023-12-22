package com.alle.assignment.presentation.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.data.repository.TextRecognizerRepository
import com.alle.assignment.domain.model.ImageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val textRecognizerRepository: TextRecognizerRepository
): ViewModel() {

    var getOCRText: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
        private set

    var getImageLabel: MutableStateFlow<Resource<List<String>>> = MutableStateFlow(Resource.Loading())
        private set

    var selectedImageEntity: MutableStateFlow<ImageEntity?> = MutableStateFlow(null)
        private set

    fun extractInfoFromImage(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                textRecognizerRepository.getOCRText(imageUri)
                    .catch {
                        getOCRText.value = Resource.Failed(it.message ?: it.toString())
                    }
                    .collect {
                        getOCRText.value = it
                        if (it is Resource.Success) {
                            fetchLatestImageEntity(imageUri.toString())
                        }
                    }
            }
            launch {
                textRecognizerRepository.getImageLabel(imageUri)
                    .catch {
                        getImageLabel.value = Resource.Failed(it.message ?: it.toString())
                    }
                    .collect {
                        getImageLabel.value = it
                        if (it is Resource.Success) {
                            fetchLatestImageEntity(imageUri.toString())
                        }
                    }
            }
        }
    }

    fun onRemoveCollectionAction(removedCollection: String) {
        viewModelScope.launch {
            textRecognizerRepository.setInactiveCollection(
                selectedImageEntity.value!!.imageName, removedCollection
            ).catch {

            }.collect {
                if (it is Resource.Success) {
                    selectedImageEntity.emit(it.data)
                }
            }
        }
    }

    fun addToActiveCollection(newActiveCollection: String) {
        viewModelScope.launch {
            textRecognizerRepository.setInactiveCollection(
                selectedImageEntity.value!!.imageName, newActiveCollection
            ).catch {

            }.collect {
                if (it is Resource.Success) {
                    selectedImageEntity.emit(it.data)
                }
            }
        }
    }

    private fun fetchLatestImageEntity(imageUri: String) {
        viewModelScope.launch {
            selectedImageEntity.emit(textRecognizerRepository.getLocalImageEntity(imageUri))
        }
    }

}
package com.alle.assignment.presentation.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.data.repository.TextRecognizerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val textRecognizerRepository: TextRecognizerRepository
): ViewModel() {

    private val _getOCRText: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
    val getOCRText: StateFlow<Resource<String>>
        get() = _getOCRText

    private val _getImageLabel: MutableStateFlow<Resource<List<String>>> = MutableStateFlow(Resource.Loading())
    val getImageLabel: StateFlow<Resource<List<String>>>
        get() = _getImageLabel

    val selectedImageCollections = arrayListOf<String>()


    fun extractInfoFromImage(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                textRecognizerRepository.getOCRText(imageUri)
                    .catch {
                        _getOCRText.value = Resource.Failed(it.message ?: it.toString())
                    }
                    .collect {
                        _getOCRText.value = it
                    }
            }
            launch {
                textRecognizerRepository.getImageLabel(imageUri)
                    .catch {
                        _getImageLabel.value = Resource.Failed(it.message ?: it.toString())
                    }
                    .collect {
                        _getImageLabel.value = it
                        if (it is Resource.Success) {
                            selectedImageCollections.clear()
                            selectedImageCollections.addAll(it.data)
                        }
                    }
            }
        }
    }

}
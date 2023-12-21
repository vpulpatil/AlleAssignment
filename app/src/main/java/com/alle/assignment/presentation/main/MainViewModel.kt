package com.alle.assignment.presentation.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.data.repository.TextRecognizerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val textRecognizerRepository: TextRecognizerRepository
): ViewModel() {

    private val _getOCRText: MutableStateFlow<Resource<String>> = MutableStateFlow(Resource.Loading())
    val getOCRText: StateFlow<Resource<String>>
        get() = _getOCRText


    fun generateOCRText(imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            textRecognizerRepository.getOCRText(imageUri)
                .catch {
                    _getOCRText.value = Resource.Failed(it.message ?: it.toString())
                }
                .collect {
                    _getOCRText.value = it
                }
        }
    }

}
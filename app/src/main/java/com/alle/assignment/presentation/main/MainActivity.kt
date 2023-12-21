package com.alle.assignment.presentation.main

import android.content.ContentUris
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.databinding.ActivityMainBinding
import com.alle.assignment.domain.model.ScreenshotModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        setupUi()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainViewModel.getOCRText.collect {
                        when(it) {
                            is Resource.Loading -> {
                                Log.d(TAG, "getOCRText Resource is loading")
                            }
                            is Resource.Success -> {
                                Log.d(TAG, "getOCRText Resource Success, data: ${it.data}")
                            }
                            is Resource.Failed -> {
                                Log.d(TAG, "getOCRText Resource Failed, errorMessage: ${it.message}")
                            }
                        }
                    }
                }
                launch {
                    mainViewModel.getImageLabel.collect {
                        when(it) {
                            is Resource.Loading -> {
                                Log.d(TAG, "getImageLabel Resource is loading")
                            }
                            is Resource.Success -> {
                                Log.d(TAG, "getImageLabel Resource Success, data: ${it.data}")
                            }
                            is Resource.Failed -> {
                                Log.d(TAG, "getImageLabel Resource Failed, errorMessage: ${it.message}")
                            }
                        }
                    }
                }

            }
        }
    }

    private fun setupUi() {
        binding.apply {
            if (isStorageAccessGiven()) {
                btnAllowStorageAccess.visibility = View.GONE
                //fetch images from screenshot folder & populate it in list
                fetchScreenshots()
            } else {
                btnAllowStorageAccess.visibility = View.VISIBLE
                btnAllowStorageAccess.setOnClickListener {
                    storageAccessAction()
                }
            }
        }
    }

    private fun isStorageAccessGiven(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()
    }

    private fun storageAccessAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivityForResult(intent, 70)
        } else {
            //TODO: Check for API lower than API 30
        }
    }

    private suspend fun getData() = withContext(Dispatchers.IO) {
        try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val selection2 = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? "
            val selection = MediaStore.Images.ImageColumns.RELATIVE_PATH + " like ? "
            val projection2 = arrayOf("Screenshots")
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.RELATIVE_PATH,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.MediaColumns.WIDTH
            )

            val selectionArgs = arrayOf("%TestApp%")
            val selectionArgs2 = arrayOf("Screenshots")

            val sortOrder = MediaStore.MediaColumns.DATE_ADDED + " COLLATE NOCASE DESC"
            val sortOrder2 = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"

            val itemList: MutableList<ScreenshotModel> = mutableListOf()

            contentResolver?.query(
                collection,
                null,
                selection2,
                selectionArgs2,
                sortOrder2
            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val relativePathColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                val widthPathColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH)


                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val relativePath = cursor.getString(relativePathColumn)
                    val width = cursor.getInt(widthPathColumn)


                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    itemList.add(ScreenshotModel(id, displayName, contentUri))

                }
                cursor.close()
            }

            itemList
        } catch (e: Exception) {
            Log.d(
                TAG, "The exception for getData is " +
                        "$e"
            )
            emptyList()
        }
    }

    private fun fetchScreenshots() {
        lifecycleScope.launch {
            val imagesList = getData()
            binding.apply {
                rvImageList.adapter = ImageListAdapter(imagesList) {
                    imgSelectedFile.setImageURI(it.fileUri)
                    mainViewModel.extractInfoFromImage(it.fileUri)
                }
            }
        }
    }
}
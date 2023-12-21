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
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alle.assignment.R
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.databinding.ActivityMainBinding
import com.alle.assignment.domain.model.ScreenshotModel
import com.alle.assignment.presentation.main.listdetail.OnBottomSheetCallbacks
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    private var listener: OnBottomSheetCallbacks? = null
    private var mBottomSheetBehavior: BottomSheetBehavior<View?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUi()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        checkForStorageAccess()
    }

    private fun setupUi() {
        binding.apply {
            bottomNavBar.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.share -> {
                        //display list screen
                        hideBottomSheet()
                        return@setOnItemSelectedListener true
                    }
                    R.id.info -> {
                        //display bottom sheet
                        configureBackdropUI()
                        return@setOnItemSelectedListener true
                    }
                }
                return@setOnItemSelectedListener false
            }
        }
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

    private fun checkForStorageAccess() {
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
            startActivity(intent)
        } else {
            //TODO: Check for API lower than API 30
        }
    }

    private suspend fun getData() = withContext(Dispatchers.IO) {
        try {
            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ? "

            val selectionArgs = arrayOf("Screenshots")

            val sortOrder = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"

            val itemList: MutableList<ScreenshotModel> = mutableListOf()

            contentResolver?.query(
                collection,
                null,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)

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

    private fun configureBackdropUI() {
        mBottomSheetBehavior?.apply {
            showBottomSheet()
            return
        }
        val fragment = supportFragmentManager.findFragmentById(R.id.filter_fragment)

        (fragment?.view?.parent as View).let { view ->
            BottomSheetBehavior.from(view).let { bs ->

                bs.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        // Call the interface to notify a state change
                        listener?.onStateChanged(bottomSheet, newState)
                    }
                })

                // Set the bottom sheet expanded by default
                bs.setPeekHeight(
                    resources.getDimensionPixelSize(R.dimen.default_bottomsheet_peek_height),
                    true
                )

                mBottomSheetBehavior = bs
                showBottomSheet()
            }
        }
    }

    fun setOnBottomSheetCallbacks(onBottomSheetCallbacks: OnBottomSheetCallbacks) {
        this.listener = onBottomSheetCallbacks
    }

    private fun hideBottomSheet() {
        binding.filterFragment.visibility = View.GONE
        binding.rvImageList.visibility = View.VISIBLE
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun showBottomSheet() {
        binding.rvImageList.visibility = View.GONE
        binding.filterFragment.visibility = View.VISIBLE
        mBottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

}
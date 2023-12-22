package com.alle.assignment.presentation.main.listdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alle.assignment.data.repository.Resource
import com.alle.assignment.databinding.FragmentListScreenBinding
import com.alle.assignment.presentation.main.MainActivity
import com.alle.assignment.presentation.main.MainViewModel
import com.alle.assignment.presentation.main.editcollection.EditCollectionBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "ListDetailFragment"
@AndroidEntryPoint
class ListDetailFragment: BottomSheetDialogFragment(), OnBottomSheetCallbacks {
    private var _binding: FragmentListScreenBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    private var currentState: Int = BottomSheetBehavior.STATE_EXPANDED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        (activity as MainActivity).setOnBottomSheetCallbacks(this)

        // Inflate the layout for this fragment
        _binding = FragmentListScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.apply {
            tvCollectionsEdit.setOnClickListener {
                val bottomsheet = EditCollectionBottomSheet.newInstance()
                bottomsheet.show(parentFragmentManager, bottomsheet.tag)
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
                                binding.tvDescriptionValue.text = it.data
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
                                setCollectionListUI(it.data)
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

    private fun setCollectionListUI(collections: List<String>) {
        binding.apply {
            rvCollectionsList.adapter = ImageCollectionAdapter(collections)
        }
    }

    // NOTE: fragments outlive their views!
    // One must clean up any references to the binging class instance here
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        Log.d("ListDetail", "onStateChanged: $newState")
        currentState = newState
        // TODO: when the bottom sheet is moving update data
        when (newState) {
            BottomSheetBehavior.STATE_EXPANDED -> {
                //state is expanded
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                //state is collapsed
            }
        }
    }
}
package com.alle.assignment.presentation.main.listdetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alle.assignment.databinding.FragmentListScreenBinding
import com.alle.assignment.presentation.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ListDetailFragment: BottomSheetDialogFragment(), OnBottomSheetCallbacks {
    private var _binding: FragmentListScreenBinding? = null
    // This property is only valid between `onCreateView` and `onDestroyView`
    private val binding get() = _binding!!

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

    // NOTE: fragments outlive their views!
    //       One must clean up any references to the binging class instance here
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
                binding.textResult.visibility = View.GONE
                binding.textResult2.visibility = View.VISIBLE
            }
            BottomSheetBehavior.STATE_COLLAPSED -> {
                //state is collapsed
                binding.textResult2.visibility = View.GONE
                binding.textResult.visibility = View.VISIBLE
            }
        }
    }
}
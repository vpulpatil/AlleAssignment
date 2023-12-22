package com.alle.assignment.presentation.main.editcollection

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.alle.assignment.R
import com.alle.assignment.databinding.BottomsheetEditCollectionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

/**
 * Created by vipul on 22/12/23
 */
class EditCollectionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mContext: Context

    private var selectedCollectionList: ArrayList<String> = arrayListOf()

    private var _binding: BottomsheetEditCollectionBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val EXTRA_SELECTED_COLLECTION_LIST = "EXTRA_SELECTED_COLLECTION_LIST"
        fun newInstance(selectedCollectionList: ArrayList<String>) : EditCollectionBottomSheet {
            val fragment = EditCollectionBottomSheet()
            fragment.arguments = bundleOf(
                EXTRA_SELECTED_COLLECTION_LIST to selectedCollectionList
            )
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        setupData()
    }

    private fun setupData() {
        selectedCollectionList.clear()
        arguments?.getStringArrayList(EXTRA_SELECTED_COLLECTION_LIST)?.let {
            selectedCollectionList.addAll(it)
        }
    }

    private fun setupUI() {
        binding.apply {
            selectedCollectionList.forEach { collection ->
                val chip = createTagChip(requireContext(), collection)
                chip.setOnCloseIconClickListener {
                    chipGrpCollections.removeView(chip)
                }
                chipGrpCollections.addView(chip)
            }
        }
    }

    private fun createTagChip(context: Context, chipName: String): Chip {
        return Chip(context).apply {
            text = chipName
            setChipBackgroundColorResource(R.color.yellow_500)
            isCloseIconVisible = true
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    private fun setupClick() {
        binding.btnDone.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetEditCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClick()
        setupUI()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (!manager.isStateSaved) {
            super.show(manager, tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

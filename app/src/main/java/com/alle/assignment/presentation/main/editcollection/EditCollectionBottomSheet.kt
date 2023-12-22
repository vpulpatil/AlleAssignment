package com.alle.assignment.presentation.main.editcollection

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alle.assignment.R
import com.alle.assignment.databinding.BottomsheetEditCollectionBinding
import com.alle.assignment.domain.model.ImageEntity
import com.alle.assignment.presentation.main.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

/**
 * Created by vipul on 22/12/23
 */
private const val TAG = "EditCollectionSheet"
class EditCollectionBottomSheet : BottomSheetDialogFragment() {

    private lateinit var mContext: Context

    private var selectedImageEntity: ImageEntity? = null

    private var _binding: BottomsheetEditCollectionBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance() : EditCollectionBottomSheet {
            return EditCollectionBottomSheet()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    private fun setupData() {
        selectedImageEntity = mainViewModel.selectedImageEntity.value
    }

    private fun setupUI(selectedImageEntity: ImageEntity?) {
        Log.d(TAG, "setupUI called")
        binding.apply {
            selectedImageEntity?.collections?.forEach { collection ->
                val chip = createActiveTagChip(mContext, collection)
                chip.setOnCloseIconClickListener {
                    mainViewModel.onRemoveCollectionAction(collection)
                    chipGrpCollections.removeView(chip)
                    addChipToInactiveCollectionList(collection)
                }
                chipGrpCollections.addView(chip)
            }
            setupInactiveCollectionUI(selectedImageEntity)
        }
    }

    private fun setupInactiveCollectionUI(selectedImageEntity: ImageEntity?) {
        Log.d(TAG, "setupInactiveCollectionUI called")
        binding.apply {
            val inactiveCollections = selectedImageEntity?.inactiveCollections ?: emptyList()
            if (inactiveCollections.isNotEmpty()) {
                tvSelectCollectionLabel.visibility = View.VISIBLE
                rvCollectionList.visibility = View.VISIBLE
                inactiveCollections.forEach {
                    val chip = createInactiveTagChip(mContext, it)
                    chip.setOnCloseIconClickListener {
                        rvCollectionList.removeView(chip)
                    }
                    rvCollectionList.addView(chip)
                }
            } else {
                tvSelectCollectionLabel.visibility = View.GONE
                rvCollectionList.visibility = View.GONE
            }
        }
    }

    private fun addChipToInactiveCollectionList(collectionName: String) {
        val chip = createInactiveTagChip(mContext, collectionName)
        chip.setOnCloseIconClickListener {
            mainViewModel.addToActiveCollection(collectionName)
            binding.rvCollectionList.removeView(chip)
            addChipToActiveCollectionList(collectionName)
        }
        binding.rvCollectionList.addView(chip)
        binding.tvSelectCollectionLabel.visibility = View.VISIBLE
        binding.rvCollectionList.visibility = View.VISIBLE
    }

    private fun addChipToActiveCollectionList(collectionName: String) {
        val chip = createActiveTagChip(mContext, collectionName)
        chip.setOnCloseIconClickListener {
            binding.chipGrpCollections.removeView(chip)
        }
        binding.chipGrpCollections.addView(chip)
    }

    private fun createActiveTagChip(context: Context, chipName: String): Chip {
        return Chip(context).apply {
            text = chipName
            setChipBackgroundColorResource(R.color.yellow_500)
            isCloseIconVisible = true
            setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    private fun createInactiveTagChip(context: Context, chipName: String): Chip {
        return Chip(context).apply {
            text = chipName
            setChipBackgroundColorResource(R.color.yellow_500)
            isCloseIconVisible = true
            closeIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_add)
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
        setupData()
        setupClick()
        setupUI(selectedImageEntity)
        setupObserver()
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    mainViewModel.selectedImageEntity.collect {
                        selectedImageEntity = it
                    }
                }
            }
        }
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

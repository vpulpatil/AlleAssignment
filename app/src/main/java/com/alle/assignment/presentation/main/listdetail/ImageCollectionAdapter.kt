package com.alle.assignment.presentation.main.listdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alle.assignment.databinding.ItemImageCollectionBinding

class ImageCollectionAdapter(
    private val collectionList: List<String>
): RecyclerView.Adapter<ImageCollectionAdapter.ImageCollectionHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageCollectionHolder {
        val binding = ItemImageCollectionBinding.inflate(LayoutInflater.from(parent.context))
        return ImageCollectionHolder(binding)
    }

    override fun getItemCount() = collectionList.size

    override fun onBindViewHolder(holder: ImageCollectionHolder, position: Int) {
        holder.bindUI(collectionList[position])
    }

    inner class ImageCollectionHolder(
        private val binding: ItemImageCollectionBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bindUI(collectionName: String) {
            binding.apply {
                tvCollectionName.text = collectionName
            }
        }
    }


}
package com.alle.assignment.presentation.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.alle.assignment.R
import com.alle.assignment.databinding.ItemImageThumbnailBinding
import com.alle.assignment.domain.model.ScreenshotModel
import com.bumptech.glide.Glide

class ImageListAdapter(
    val imageList: List<ScreenshotModel>,
    val imageClickAction: (clickedImage: ScreenshotModel) -> Unit
) : RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageListViewHolder {
        val binding = ItemImageThumbnailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageListViewHolder(binding).apply {
            itemView.findViewById<ImageView>(R.id.imgThumbnail).setOnClickListener {
                imageClickAction.invoke(imageList[adapterPosition])
            }
        }
    }

    override fun getItemCount() = imageList.size

    override fun onBindViewHolder(holder: ImageListViewHolder, position: Int) {
        holder.bindUI(imageList[position])
    }


    inner class ImageListViewHolder(
        val binding: ItemImageThumbnailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindUI(screenshotModel: ScreenshotModel) {
            binding.apply {
                Glide.with(itemView)
                    .load(screenshotModel.fileUri)
                    .centerCrop()
                    .into(imgThumbnail)
//                imgThumbnail.setImageURI(screenshotModel.fileUri)
            }
        }

    }
}
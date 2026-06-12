package com.capstone.eyesonme.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Note: Add Glide dependency in build.gradle
import com.capstone.eyesonme.R

class ImageAdapter(
    private var images: List<ImageItem>,
    private val onItemClick: (ImageItem) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    fun updateData(newImages: List<ImageItem>) {
        this.images = newImages
        notifyDataSetChanged() // Perbarui tampilan
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_item_photo)
        private val titleView: TextView = itemView.findViewById(R.id.tv_item_title)

        fun bind(image: ImageItem) {
            Glide.with(itemView.context)
                .load(image.imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .into(imageView)

            titleView.text = image.title

            itemView.setOnClickListener { onItemClick(image) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount() = images.size
}

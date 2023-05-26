package com.example.finalprojecte.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.finalprojecte.databinding.ViewpagerImageItemBinding

class ViewPager2Image : RecyclerView.Adapter<ViewPager2Image.ViewPager2ImageHolder>() {
    inner class ViewPager2ImageHolder(private val binding: ViewpagerImageItemBinding) : ViewHolder(binding.root) {
        fun bind(imagePath: String) {
            Glide.with(itemView).load(imagePath).into(binding.imageViewProductDetails)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImageHolder {
        return ViewPager2ImageHolder(
            ViewpagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPager2ImageHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
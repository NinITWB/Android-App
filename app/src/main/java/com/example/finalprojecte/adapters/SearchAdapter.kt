package com.example.finalprojecte.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.databinding.FragmentSearchBinding
import com.example.finalprojecte.databinding.SearchItemBinding

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    inner class SearchViewHolder(private val binding: SearchItemBinding) : ViewHolder(binding.root) {
        fun bind(product: Products?) {
            binding.apply {
                Glide.with(itemView).load(product!!.images[0]).into(imageCartProduct)
                tvProductCartName.text = product!!.name
            }
        }

    }



    private val diffCallBack = object : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }

    }

    var differ = AsyncListDiffer(this, diffCallBack)

    fun setFilteredList(dataList: List<Products>) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            SearchItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    var onClick : ((Products?) -> Unit)? = null

}
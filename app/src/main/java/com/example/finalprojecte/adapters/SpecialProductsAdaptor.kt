package com.example.finalprojecte.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.databinding.SpecialRvItemBinding
import java.text.DecimalFormat

class SpecialProductsAdaptor : RecyclerView.Adapter<SpecialProductsAdaptor.SpecialProductsViewHolder>() {
    inner class SpecialProductsViewHolder(val binding: SpecialRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Products) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgSpecialItem)
                TextViewSpecialProductsName.text = product.name
                TextViewSpecialProductsPrice.text = "${convertCurrency(product.price)} VND"
            }
        }

        private fun convertCurrency(num: Float) : String {
            val formatter = DecimalFormat("#,###");
            val formattedNumber = formatter.format(num);
            return formattedNumber
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val products = differ.currentList[position]
        holder.bind(products)

        holder.itemView.setOnClickListener {
            onClick?.invoke(products)
        }

        holder.binding.btnAddToCart.setOnClickListener {
            onAddButton?.invoke(products)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Products) -> Unit)? = null
    var onAddButton: ((Products) -> Unit)? = null
}
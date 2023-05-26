package com.example.finalprojecte.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.databinding.BestDealsRvItemBinding
import com.example.finalprojecte.databinding.ProductRvItemBinding
import java.text.DecimalFormat

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {
    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Products) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestProduct)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvOldPrice.text = "${convertCurrency(priceAfterOffer)} VND"
                    TextViewBestProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (product.offerPercentage == null) tvOldPrice.visibility = View.INVISIBLE
                TextViewBestProductPrice.text = "${convertCurrency(product.price)}"
                TextViewBestProductName.text = product.name
            }
        }

        private fun convertCurrency(num: Float) : String {
            val formatter = DecimalFormat("#,###");
            val formattedNumber = formatter.format(num);
            return formattedNumber
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

    val differ = AsyncListDiffer(this, diffCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Products) -> Unit)? = null
}
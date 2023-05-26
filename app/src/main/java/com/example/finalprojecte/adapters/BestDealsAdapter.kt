package com.example.finalprojecte.adapters

import android.graphics.Paint
import android.media.browse.MediaBrowser.ItemCallback
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.databinding.BestDealsRvItemBinding
import com.example.finalprojecte.databinding.FragmentMainCategoriesBinding
import java.text.DecimalFormat

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {
    inner class BestDealsViewHolder(private val binding: BestDealsRvItemBinding) : ViewHolder(binding.root) {
        fun bind(product: Products) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeals)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvOldPrice.text = "${convertCurrency(priceAfterOffer)} VND"
                    TextViewProductsPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (product.offerPercentage == null) tvOldPrice.visibility = View.INVISIBLE
                TextViewProductsPrice.text = "${convertCurrency(product.price)}"
                TextViewDealProductsName.text = product.name
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
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
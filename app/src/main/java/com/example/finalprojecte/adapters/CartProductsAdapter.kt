package com.example.finalprojecte.adapters

import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalprojecte.data.CartProducts
import com.example.finalprojecte.databinding.CartProductItemBinding
import java.text.DecimalFormat

class CartProductsAdapter : RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {
    inner class CartProductsViewHolder(val binding: CartProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProducts) {
            binding.apply {
                Glide.with(itemView).load(cartProduct.products.images[0]).into(imageCartProduct)
                tvProductCartName.text = cartProduct.products.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()
                cartProduct.products.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * cartProduct.products.price
                    tvProductCartPrice.text = "${convertCurrency(priceAfterOffer)} VND"
                }
            }
        }

        private fun convertCurrency(num: Float) : String {
            val formatter = DecimalFormat("#,###");
            val formattedNumber = formatter.format(num);
            return formattedNumber
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.products.id == newItem.products.id
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onProductClick: ((CartProducts) -> Unit)? = null
    var onPlusClick: ((CartProducts) -> Unit)? = null
    var onMinusClick: ((CartProducts) -> Unit)? = null
}
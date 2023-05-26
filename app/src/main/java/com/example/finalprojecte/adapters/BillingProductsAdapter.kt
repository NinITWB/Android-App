package com.example.finalprojecte.adapters

import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.example.finalprojecte.data.CartProducts
import com.example.finalprojecte.databinding.BillingProductsRvItemBinding
import com.example.finalprojecte.helper.getProductsPrice
import java.text.DecimalFormat

class BillingProductsAdapter : Adapter<BillingProductsAdapter.BillingProductsViewHolder>() {
    inner class BillingProductsViewHolder(val binding: BillingProductsRvItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(billingProduct: CartProducts) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.products.images[0]).into(imageCartProduct)
                tvProductCartName.text = billingProduct.products.name

                val priceAfterOffer = billingProduct.products.offerPercentage.getProductsPrice(billingProduct.products.price)
                tvProductCartPrice.text = "${convertCurrency(priceAfterOffer)} VND"
            }


        }

        private fun convertCurrency(num: Float) : String {
            val formatter = DecimalFormat("#,###");
            val formattedNumber = formatter.format(num);
            return formattedNumber
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProducts>(){
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]

        holder.bind(billingProduct)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
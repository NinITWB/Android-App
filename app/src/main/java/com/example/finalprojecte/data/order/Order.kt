package com.example.finalprojecte.data.order

import com.example.finalprojecte.data.Address
import com.example.finalprojecte.data.CartProducts

data class Order(
    val orderStatus: String,
    val totalPrice: Float,
    val products: List<CartProducts>,
    val address: Address
)
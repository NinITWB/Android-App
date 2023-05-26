package com.example.finalprojecte.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProducts(
    val products: Products,
    val quantity: Int
) : Parcelable {
    constructor() : this(Products(), 1)
}

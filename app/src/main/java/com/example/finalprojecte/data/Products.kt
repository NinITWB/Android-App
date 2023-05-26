package com.example.finalprojecte.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Products(
    val id: String,
    val name: String,
    val category: String,
    val price: Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val images: List<String>
) : Parcelable {
    constructor() : this("0", "", "", 0f, 0f, "", images = emptyList())
}

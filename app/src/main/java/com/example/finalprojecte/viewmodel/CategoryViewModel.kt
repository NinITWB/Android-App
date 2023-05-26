package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.data.Categories
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.utility.Resources
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class CategoryViewModel constructor(
    private val firestore: FirebaseFirestore,
    private val categories: Categories
) : ViewModel() {

    private val _offerProducts = MutableStateFlow<Resources<List<Products>>>(Resources.Unspecified())
    val offerProducts = _offerProducts.asStateFlow()

    private val _bestProduct = MutableStateFlow<Resources<List<Products>>>(Resources.Unspecified())
    val bestProducts = _bestProduct.asStateFlow()

    private val pagingInfo = BasePagingInfo()
    init {
        fetchOfferProducts()
        fetchBestProducts()
    }

    fun fetchOfferProducts() {
        if (!pagingInfo.isPagingOffer) {
            viewModelScope.launch { _offerProducts.emit(Resources.Loading()) }
            firestore.collection("Products")
                .whereEqualTo("category", categories.category)
                .whereNotEqualTo("offerPercentage", null).limit(pagingInfo.baseOfferProducts * 10)
                .get()
                .addOnSuccessListener {
                    val products = it.toObjects(Products::class.java)
                    pagingInfo.isPagingOffer = products == pagingInfo.baseOldOfferProducts
                    pagingInfo.baseOldOfferProducts = products
                    viewModelScope.launch {
                        _offerProducts.emit(Resources.Success(products))
                    }
                    pagingInfo.baseOfferProducts++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _offerProducts.emit(Resources.Error(it.message.toString()))
                    }
                }
        }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingBest) {
            viewModelScope.launch { _bestProduct.emit(Resources.Loading()) }
            firestore.collection("Products")
                .whereEqualTo("category", categories.category)
                .whereEqualTo("offerPercentage", null).limit(pagingInfo.baseBestProducts * 10).get()
                .addOnSuccessListener {
                    val products = it.toObjects(Products::class.java)
                    pagingInfo.isPagingBest = products == pagingInfo.baseOldBestProducts
                    pagingInfo.baseOldBestProducts = products
                    viewModelScope.launch {
                        _bestProduct.emit(Resources.Success(products))
                    }
                    pagingInfo.baseBestProducts++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProduct.emit(Resources.Error(it.message.toString()))
                    }
                }
        }
    }
}

internal data class BasePagingInfo(
    var baseBestProducts: Long = 1,
    var baseOfferProducts: Long = 1,
    var baseOldBestProducts: List<Products> = emptyList(),
    var baseOldOfferProducts: List<Products> = emptyList(),
    var isPagingBest: Boolean = false,
    var isPagingOffer: Boolean = false
)
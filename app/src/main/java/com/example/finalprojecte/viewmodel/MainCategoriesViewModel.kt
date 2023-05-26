package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.utility.Resources
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoriesViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _specialProducts = MutableStateFlow<Resources<List<Products>>>(Resources.Unspecified())
    val specialProducts: StateFlow<Resources<List<Products>>> = _specialProducts

    private val _bestDealProducts = MutableStateFlow<Resources<List<Products>>>(Resources.Unspecified())
    val bestDealProducts: StateFlow<Resources<List<Products>>> = _bestDealProducts

    private val _bestProducts = MutableStateFlow<Resources<List<Products>>>(Resources.Unspecified())
    val bestProducts: StateFlow<Resources<List<Products>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fectSpecialProducts()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fectSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resources.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Special Products")
            .get().addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Products::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resources.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDealProducts.emit(Resources.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("category", "Best Deals").get()
            .addOnSuccessListener { result ->
                val bestDealsList = result.toObjects(Products::class.java)
                viewModelScope.launch {
                    _bestDealProducts.emit(Resources.Success(bestDealsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealProducts.emit(Resources.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resources.Loading())
            }
            firestore.collection("Products")
                .whereEqualTo("category", "Best Products").limit(pagingInfo.bestProductPage * 10)
                .get()
                .addOnSuccessListener { result ->
                    val bestProductsList = result.toObjects(Products::class.java)
                    pagingInfo.isPagingEnd = bestProductsList == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = bestProductsList
                    viewModelScope.launch {
                        _bestProducts.emit(Resources.Success(bestProductsList))
                    }
                    pagingInfo.bestProductPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resources.Error(it.message.toString()))
                    }
                }
        }
    }
}

internal data class PagingInfo(
    var bestProductPage: Long = 1,
    var oldBestProducts: List<Products> = emptyList(),
    var isPagingEnd: Boolean = false
)
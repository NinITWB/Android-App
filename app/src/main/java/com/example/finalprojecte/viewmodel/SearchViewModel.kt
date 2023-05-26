package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.utility.Resources
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    private val _searchItem = MutableStateFlow<Resources<List<Products>>>(Resources.Unspecified())
    val searchItem = _searchItem.asStateFlow()
    var productData = mutableListOf<Products>()


    init {
        fetchSearchItem()
    }


    private fun fetchSearchItem() {
        viewModelScope.launch { _searchItem.emit(Resources.Loading()) }
        firestore.collection("Products").whereNotEqualTo("category", null)
                .get().addOnSuccessListener { result ->
                    val product = result.toObjects(Products::class.java)
                    productData = product
                    viewModelScope.launch {
                        _searchItem.emit(Resources.Success(productData))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _searchItem.emit(Resources.Error(it.message.toString()))
                    }
                }


    }
}
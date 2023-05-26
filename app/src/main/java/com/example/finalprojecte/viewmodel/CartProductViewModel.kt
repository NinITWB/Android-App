package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.data.CartProducts
import com.example.finalprojecte.firebase.FireBaseFunction
import com.example.finalprojecte.utility.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartProductViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val fireBaseFunction: FireBaseFunction
) : ViewModel() {
    private val _addToCart = MutableStateFlow<Resources<CartProducts>>(Resources.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun updateCartProducts(cartProducts: CartProducts) {
        viewModelScope.launch { _addToCart.emit(Resources.Loading()) }
        firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
                .whereEqualTo("products.id", cartProducts.products.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) {
                        addNewProduct(cartProducts)
                    } else {
                        val product = it.first().toObject(CartProducts::class.java)
                        if (product == cartProducts) {
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProducts)
                        } else {
                            addNewProduct(cartProducts)
                        }
                    }

                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resources.Error(it.message.toString())) }
            }
    }

    private fun addNewProduct(cartProducts: CartProducts) {
        fireBaseFunction.addProductToCart(cartProducts) { addedProduct, exception ->
            viewModelScope.launch {
                if (exception == null) _addToCart.emit(Resources.Success(addedProduct!!))
                else _addToCart.emit(Resources.Error(exception.message.toString()))
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProducts: CartProducts) {
        fireBaseFunction.increaseQuantity(documentId) { _, exception ->
            viewModelScope.launch {
                if (exception == null) _addToCart.emit(Resources.Success(cartProducts))
                else _addToCart.emit(Resources.Error(exception.message.toString()))
            }
        }
    }

}
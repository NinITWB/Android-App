package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.data.CartProducts
import com.example.finalprojecte.firebase.FireBaseFunction
import com.example.finalprojecte.utility.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import com.example.finalprojecte.helper.getProductsPrice
import javax.inject.Inject

@HiltViewModel
class ManageCartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val fireBaseFunction: FireBaseFunction
) : ViewModel() {
    private val productCart = CartProducts()
    private val _cartProducts = MutableStateFlow<Resources<List<CartProducts>>>(Resources.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<CartProducts>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var documentCart = emptyList<DocumentSnapshot>()

    val productsPrice = cartProducts.map {
        when (it) {
            is Resources.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }

    fun deleteCartProduct(cartProduct: CartProducts) {
        val index = _cartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val documentId = documentCart[index].id
            firestore.collection("user").document(firebaseAuth.uid!!).collection("cart").document(documentId).delete()
        }
    }

    private fun calculatePrice(data: List<CartProducts>): Float {
        return data.sumByDouble { cartProducts ->
            (cartProducts.products.offerPercentage.getProductsPrice(cartProducts.products.price) * cartProducts.quantity).toDouble()
        }.toFloat()
    }

    init {
        getCartProducts()
    }


    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resources.Loading())
        }
        firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
                .addSnapshotListener { value, error ->
                    if (error != null || value == null) viewModelScope.launch {
                        _cartProducts.emit(Resources.Error(error?.message.toString()))
                    } else {
                        documentCart = value.documents
                        val cartProducts = value.toObjects(CartProducts::class.java)
                        viewModelScope.launch { _cartProducts.emit(Resources.Success(cartProducts)) }
                    }

                }
    }

    fun changeQuantity(
        cartProduct: CartProducts,
        quantityChanging: FireBaseFunction.QuantityChanging
    ) {
        val index = cartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val documentId = documentCart[index].id
            when (quantityChanging) {
                FireBaseFunction.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resources.Loading()) }
                    increaseQuantity(documentId)
                }
                FireBaseFunction.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resources.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        fireBaseFunction.decreaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resources.Error(exception.message.toString())) }
        }
    }

    private fun increaseQuantity(documentId: String) {
        fireBaseFunction.increaseQuantity(documentId) { result, exception ->
            if (exception != null)
                viewModelScope.launch { _cartProducts.emit(Resources.Error(exception.message.toString())) }
        }
    }
}
package com.example.finalprojecte.firebase

import com.example.finalprojecte.data.CartProducts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FireBaseFunction(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val cartCollection = firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")

    fun addProductToCart(cartProducts: CartProducts, onResult: (CartProducts?, Exception?) -> Unit) {
        cartCollection.document().set(cartProducts).addOnSuccessListener {
            onResult(cartProducts, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProducts::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun decreaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transaction ->
            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProducts::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChanging {
        INCREASE, DECREASE
    }
}
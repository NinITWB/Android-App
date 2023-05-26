package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.data.order.Order
import com.example.finalprojecte.utility.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private var firestore: FirebaseFirestore,
    private var firebaseAuth: FirebaseAuth
) : ViewModel() {
    private var _order = MutableStateFlow<Resources<Order>>(Resources.Unspecified())
    var order = _order.asStateFlow()

    fun placeOrder(order: Order) {
        viewModelScope.launch { _order.emit(Resources.Loading()) }
        firestore.runBatch { batch ->
            firestore.collection("user")
                .document(firebaseAuth.uid!!)
                .collection("orders")
                .document()
                .set(order)

            firestore.collection("orders").document().set(order)

            firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
                .get().addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resources.Success(order))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resources.Error(it.message.toString()))
            }
        }
    }
}
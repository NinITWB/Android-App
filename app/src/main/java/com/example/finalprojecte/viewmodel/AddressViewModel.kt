package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.data.Address
import com.example.finalprojecte.utility.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private var firestore: FirebaseFirestore,
    private var firebaseAuth: FirebaseAuth
) : ViewModel() {
    private var _addNewAddress = MutableStateFlow<Resources<Address>>(Resources.Unspecified())
    var addNewAddress = _addNewAddress.asStateFlow()

    private var _error = MutableSharedFlow<String>()
    var error = _error.asSharedFlow()

    fun addAddress(address: Address) {
        val validateInputs = validateInputs(address)
        if (validateInputs) {
            viewModelScope.launch { _addNewAddress.emit(Resources.Loading()) }
            firestore.collection("user").document(firebaseAuth.uid!!).collection("address")
                .document()
                .set(address).addOnSuccessListener {
                    viewModelScope.launch { _addNewAddress.emit(Resources.Success(address)) }
                }.addOnFailureListener {
                    viewModelScope.launch { _addNewAddress.emit(Resources.Error(it.message.toString())) }
                }
        } else {
            viewModelScope.launch {
                _error.emit("All fields are filled totally")
            }
        }
    }

    private fun validateInputs(address: Address): Boolean {
        return address.addressTitle.trim().isNotEmpty() &&
                address.fullName.trim().isNotEmpty() &&
                address.phone.trim().isNotEmpty() &&
                address.city.trim().isNotEmpty() &&
                address.street.trim().isNotEmpty()
    }
}
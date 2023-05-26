package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.example.finalprojecte.data.User
import com.example.finalprojecte.utility.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _account = MutableStateFlow<Resources<User>>(Resources.Unspecified())
    val account = _account.asStateFlow()

    init {
        getAccount()
    }

    fun getAccount() {
        viewModelScope.launch { _account.emit(Resources.Loading()) }
        firestore.collection("user").document(firebaseAuth.uid!!)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        viewModelScope.launch {
                            _account.emit(Resources.Error(error.message.toString()))
                        }
                    } else {
                        val user = value?.toObject(User::class.java)
                        user?.let {
                            viewModelScope.launch {
                                _account.emit(Resources.Success(user))
                            }
                        }
                    }
                }
    }

    fun logOut() {
        firebaseAuth.signOut()
    }
}
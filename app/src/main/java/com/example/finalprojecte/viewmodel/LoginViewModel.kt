package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.utility.Resources
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authentication: FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resources<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resources<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun loginAuthenticate(email: String, password: String) {
        viewModelScope.launch { _login.emit(Resources.Loading()) }
        authentication.signInWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            viewModelScope.launch {
                it.user?.let {
                    _login.emit(Resources.Success(it))
                }
            }
        }.addOnFailureListener() {
            viewModelScope.launch {
                _login.emit(Resources.Error(it.message.toString()))
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resources.Loading())
        }

        authentication.sendPasswordResetEmail(email).addOnSuccessListener {
            viewModelScope.launch {
                _resetPassword.emit(Resources.Success(email))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _resetPassword.emit(Resources.Error(it.message.toString()))
            }
        }
    }
}
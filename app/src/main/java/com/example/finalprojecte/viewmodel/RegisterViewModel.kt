package com.example.finalprojecte.viewmodel

import androidx.lifecycle.ViewModel
import com.example.finalprojecte.data.User
import com.example.finalprojecte.utility.*
import com.example.finalprojecte.utility.Constanst.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val dataBase: FirebaseFirestore
) : ViewModel() {
    private val _register = MutableStateFlow<Resources<User>>(Resources.Unspecified())
    val register: Flow<Resources<User>> = _register

    private val _validation = Channel<RegisterFieldStates>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resources.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
                it.user?.let {
                    saveUserInfo(it.uid, user)
                }
            }.addOnFailureListener() {
                _register.value = Resources.Error(it.message.toString())
            }
        } else {
            val registerState = RegisterFieldStates(validateEmail(user.email), validatePassword(password))

            runBlocking {
                _validation.send(registerState)
            }
        }
    }

    private fun saveUserInfo(userUID: String, user: User) {
        dataBase.collection(USER_COLLECTION)
                .document(userUID)
                .set(user)
            .addOnSuccessListener {
                _register.value = Resources.Success(user)
            }.addOnFailureListener() {
                _register.value = Resources.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String) : Boolean {
        val emailValidate = validateEmail(user.email)
        val passValidate = validatePassword(password)
        val shouldRegister =
            emailValidate is RegisterValidation.Success && passValidate is RegisterValidation.Success

        return shouldRegister
    }
}
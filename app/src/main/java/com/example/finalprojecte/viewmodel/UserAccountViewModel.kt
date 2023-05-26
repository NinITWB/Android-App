package com.example.finalprojecte.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojecte.NinApplication
import com.example.finalprojecte.data.User
import com.example.finalprojecte.utility.RegisterValidation
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.utility.validateEmail

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application
) : AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resources<User>>(Resources.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resources<User>>(Resources.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch { _user.emit(Resources.Loading()) }
        firestore.collection("user").document(firebaseAuth.uid!!).get()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        val user = it.toObject(User::class.java)
                        user?.let {
                            viewModelScope.launch {
                                _user.emit(Resources.Success(user))
                            }
                        }
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _user.emit(Resources.Error(it.message.toString()))
                    }
                }
    }

    fun updateUser(user: User, imageUrl: Uri?) {
        val areInputValid = validateEmail(user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if (!areInputValid) {
            viewModelScope.launch {
                _updateInfo.emit(Resources.Error("Please check your input"))
            }
            return
        }
        viewModelScope.launch { _updateInfo.emit(Resources.Loading()) }
        if (imageUrl == null) {
            saveUserInformation(user, true)
        } else {
            saveUserInformationWithNewImage(user, imageUrl)
        }
    }

    private fun saveUserInformationWithNewImage(user: User, imageUrl: Uri) {
        viewModelScope.launch {
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<NinApplication>().contentResolver, imageUrl)
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()
                val imageDirectory = storage.child("profileImages/${firebaseAuth.uid}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val ImageUrl = result.storage.downloadUrl.await().toString()
                saveUserInformation(user.copy(imagePath = ImageUrl), false)
            } catch (e: Exception) {
                viewModelScope.launch {
                    _user.emit(Resources.Error(e.message.toString()))
                }
            }
        }
    }

    private fun saveUserInformation(user: User, shouldRetrieveOldImage: Boolean) {
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(firebaseAuth.uid!!)
            if (shouldRetrieveOldImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.emit(Resources.Success(user))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _user.emit(Resources.Error(it.message.toString()))
            }
        }
    }
}
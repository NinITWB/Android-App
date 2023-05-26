package com.example.finalprojecte.utility

sealed class RegisterValidation() {
    object Success : RegisterValidation()
    data class Failed(val message: String) : RegisterValidation()
}

data class RegisterFieldStates(val email: RegisterValidation, val password: RegisterValidation)

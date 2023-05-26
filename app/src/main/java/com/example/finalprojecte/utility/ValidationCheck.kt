package com.example.finalprojecte.utility

import android.util.Patterns
import java.util.regex.Pattern

fun validateEmail(email: String) : RegisterValidation {
    if (email.isEmpty()) return RegisterValidation.Failed("Email can't be empty")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(pass: String) : RegisterValidation {
    val passREGEX = Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-z])" +         //at least 1 lower case letter
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=\\S+$)" +           //no white spaces
            ".{6,}" +               //at least 6 characters
            "$")
    if (!passREGEX.matcher(pass).matches()) return RegisterValidation.Failed("Wrong password format. Please check again!")
    return RegisterValidation.Success
}
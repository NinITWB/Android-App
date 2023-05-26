package com.example.finalprojecte.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalprojecte.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

    }
}
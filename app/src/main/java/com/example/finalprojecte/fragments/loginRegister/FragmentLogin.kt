package com.example.finalprojecte.fragments.loginRegister

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalprojecte.R
import com.example.finalprojecte.activities.ShoppingActivities
import com.example.finalprojecte.databinding.FragmentLoginBinding
import com.example.finalprojecte.dialog.setupBottomsheetDialog
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentLogin : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.SuggestionRegis.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_Login_to_fragment_Register)
        }

        binding.forgotPassword.setOnClickListener {
            setupBottomsheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when(it) {
                    is Resources.Loading -> {
                    }
                    is Resources.Success -> {
                       val snackbar = Snackbar.make(requireView(), "A link to rest password has sent to your email", Snackbar.LENGTH_LONG)
                       val snackBarView: View
                       snackBarView = snackbar.view

                    }
                    is Resources.Error -> {
                        Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            buttonLogin.setOnClickListener {
                val email = userName.text.toString().trim()
                val pass = password.text.toString()
                viewModel.loginAuthenticate(email, pass)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when(it) {
                    is Resources.Loading -> {
                        binding.buttonLogin.startAnimation()
                    }
                    is Resources.Success -> {
                        binding.buttonLogin.revertAnimation()
                        Intent(requireActivity(), ShoppingActivities::class.java).also { intent->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonLogin.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }
}
package com.example.finalprojecte.fragments.loginRegister

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.finalprojecte.R
import com.example.finalprojecte.data.User
import com.example.finalprojecte.databinding.FragmentRegisterBinding
import com.example.finalprojecte.utility.RegisterValidation
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.viewmodel.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = "FragmentRegister"
@AndroidEntryPoint
class FragmentRegister : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.Suggestion.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_Register_to_fragment_Login)
        }

        binding.apply {
            buttonRegister.setOnClickListener {
                val user = User(
                    EdfirstName.text.toString().trim(),
                    EdlastName.text.toString().trim(),
                    EduserName.text.toString().trim()
                )
                val password = edPassword.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when(it) {
                    is Resources.Loading -> {
                        binding.buttonRegister.startAnimation()
                    }
                    is Resources.Success -> {
                        Log.d("test", it.data.toString())
                        binding.buttonRegister.revertAnimation()
                        findNavController().navigate(R.id.action_fragment_Register_to_fragment_Login)
                        val message: String = it.message.toString()
                        val duration = Toast.LENGTH_LONG
                        val toast = Toast.makeText(requireContext(), message, duration)
                        toast.setGravity(Gravity.CENTER and Gravity.TOP, 0, 0)
                        toast.show()
                    }
                    is Resources.Error -> {
                        Log.e(TAG, it.message.toString())
                        binding.buttonRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.validation.collect { validate ->
                if (validate.email is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.EduserName.apply {
                            requestFocus()
                            error = validate.email.message
                        }
                    }
                }

                if (validate.password is RegisterValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.edPassword.apply {
                            requestFocus()
                            error = validate.password.message
                        }
                    }
                }
            }
        }
    }
}
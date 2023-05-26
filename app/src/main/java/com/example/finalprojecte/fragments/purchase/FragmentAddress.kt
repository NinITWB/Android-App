package com.example.finalprojecte.fragments.purchase

import android.annotation.SuppressLint
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
import com.example.finalprojecte.data.Address
import com.example.finalprojecte.databinding.FragmentAddressBinding
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FragmentAddress : Fragment(R.layout.fragment_address) {
    private lateinit var binding: FragmentAddressBinding
    val viewModel by viewModels<AddressViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        binding.progressbarAddress.visibility = View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resources.Error -> {
                        val layout = layoutInflater.inflate(R.layout.custome_toast_error, null)
                        val message = layout.findViewById<TextView>(R.id.toast_text_error)
                        message.text = it.message.toString()
                        Toast(requireContext()).apply {
                            duration = Toast.LENGTH_LONG
                            setGravity(Gravity.TOP, 0, 0)
                            setView(layout)
                        }.show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                val layout = layoutInflater.inflate(R.layout.custome_toast_error, null)
                val message = layout.findViewById<TextView>(R.id.toast_text_error)
                message.text = it
                Toast(requireContext()).apply {
                    duration = Toast.LENGTH_LONG
                    setGravity(Gravity.TOP, 0, 0)
                    setView(layout)
                }.show()
            }
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageAddressClose.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle = edAddressTitle.text.toString()
                val fullName = edFullName.text.toString()
                val street = edStreet.text.toString()
                val phone = edPhone.text.toString()
                val city = edCity.text.toString()
                val address = Address(addressTitle, fullName, street, phone, city)
                viewModel.addAddress(address)

            }

            buttonDelelte.setOnClickListener {
                edAddressTitle.text.clear()
                edFullName.text.clear()
                edStreet.text.clear()
                edPhone.text.clear()
                edCity.text.clear()
            }
        }
    }
}
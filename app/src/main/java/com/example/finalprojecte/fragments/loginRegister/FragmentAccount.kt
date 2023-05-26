package com.example.finalprojecte.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalprojecte.R
import com.example.finalprojecte.databinding.FragmentAccountBinding

class FragmentAccount : Fragment(R.layout.fragment_account) {
    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAccountLogin.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_Account_to_fragment_Login)
        }

        binding.buttonAccountRegister.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_Account_to_fragment_Register)
        }
    }
}
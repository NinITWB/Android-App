package com.example.finalprojecte.fragments.loginRegister

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalprojecte.R
import com.example.finalprojecte.databinding.FragmentIntroductionBinding

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentIntroduction : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding: FragmentIntroductionBinding
    //private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_Introduction_to_fragment_Account)
        }
    }
}
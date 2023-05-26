package com.example.finalprojecte.fragments.purchase

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.finalprojecte.BuildConfig
import com.example.finalprojecte.R
import com.example.finalprojecte.activities.LoginRegister
import com.example.finalprojecte.databinding.FragmentProfileBinding
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.utility.showBottomNavigation
import com.example.finalprojecte.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FragmentProfile : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfile_to_userAccountFragment)
        }

        binding.linearLogOut.setOnClickListener {
            viewModel.logOut()
            val Intent = Intent(requireActivity(), LoginRegister::class.java)
            startActivity(Intent)
            requireActivity().finish()
        }

        binding.tvAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfile_to_fragmentCart)
        }

        binding.tvBilling.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentProfile_to_billingFragment)
        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"
        lifecycleScope.launchWhenStarted {
            viewModel.account.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(
                            ColorDrawable(
                            Color.BLACK)
                        ).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"
                    }
                    is Resources.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.progressbarSettings.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }

}
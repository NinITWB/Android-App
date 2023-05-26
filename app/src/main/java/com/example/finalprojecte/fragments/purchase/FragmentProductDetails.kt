package com.example.finalprojecte.fragments.purchase

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
import androidx.navigation.fragment.navArgs
import com.example.finalprojecte.R
import com.example.finalprojecte.adapters.ViewPager2Image
import com.example.finalprojecte.data.CartProducts
import com.example.finalprojecte.databinding.FragmentProductDetailsBinding
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.utility.hideBottomNavigation
import com.example.finalprojecte.viewmodel.CartProductViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.DecimalFormat


@AndroidEntryPoint
class FragmentProductDetails : Fragment() {
    private val args by navArgs<FragmentProductDetailsArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Image() }
    private val viewModel by viewModels<CartProductViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigation()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = args.product


        setUpViewPager()
        binding.closeImage.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.updateCartProducts(CartProducts(product, quantity = 1))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resources.Success -> {
                        binding.buttonAddToCart.revertAnimation()

                        val layout = layoutInflater.inflate(R.layout.custom_toast, null)
                        val message = layout.findViewById<TextView>(R.id.textToast)
                        message.text = "You added product to your cart"
                        Toast(requireContext()).apply {
                            duration = Toast.LENGTH_LONG
                            setGravity(Gravity.TOP, 0, 0)
                            setView(layout)
                        }.show()
                    }
                    is Resources.Error -> {
                        binding.buttonAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            TextViewProductName.text = product.name
            TextViewProductPrice.text = "${convertCurrency(product.price)}"
            TextViewDescription.text = product.description
        }

        viewPagerAdapter.differ.submitList(product.images)
    }

    private fun convertCurrency(num: Float) : String {
        val formatter = DecimalFormat("#,###");
        val formattedNumber = formatter.format(num);
        return formattedNumber
    }

    private fun setUpViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }
}
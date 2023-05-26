package com.example.finalprojecte.fragments.categories

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojecte.R
import com.example.finalprojecte.adapters.BestDealsAdapter
import com.example.finalprojecte.adapters.BestProductsAdapter
import com.example.finalprojecte.adapters.SpecialProductsAdaptor
import com.example.finalprojecte.data.CartProducts
import com.example.finalprojecte.databinding.FragmentMainCategoriesBinding
import com.example.finalprojecte.fragments.purchase.FragmentProductDetailsArgs
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.utility.showBottomNavigation
import com.example.finalprojecte.viewmodel.CartProductViewModel
import com.example.finalprojecte.viewmodel.MainCategoriesViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "FragmentMainCategories"
@AndroidEntryPoint
class FragmentMainCategories : Fragment(R.layout.fragment_main_categories) {
    private lateinit var binding: FragmentMainCategoriesBinding
    private lateinit var specialProductsAdaptor: SpecialProductsAdaptor
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private val viewModel by viewModels<MainCategoriesViewModel>()

    private val cartViewModel by viewModels<CartProductViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoriesBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setUpSpecialProductsRecyclerView()
        setUpBestDealsRv()
        setUpBestProductRv()

        specialProductsAdaptor.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentProductDetails, b)
        }

        bestDealsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentProductDetails, b)
        }

        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentProductDetails, b)
        }

        specialProductsAdaptor.onAddButton = {
            cartViewModel.updateCartProducts(CartProducts(products = it, quantity = 1))
            val snackbar = Snackbar.make(requireView(), "You've added product", Snackbar.LENGTH_SHORT)
            snackbar.view.setBackgroundColor(Color.parseColor("#9ED19A"))
            snackbar.setTextColor(Color.parseColor("#1D1E1D"))
            snackbar.show()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        showLoading()
                    }
                    is Resources.Success -> {
                        specialProductsAdaptor.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resources.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealProducts.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        showLoading()
                    }
                    is Resources.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resources.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        binding.bestProductProgressBar.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.bestProductProgressBar.visibility = View.GONE
                    }
                    is Resources.Error -> {
                        binding.bestProductProgressBar.visibility = View.GONE
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.nestedViewMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                viewModel.fetchBestProducts()
            }
        })
    }

    private fun setUpBestProductRv() {
        bestProductsAdapter = BestProductsAdapter()
        binding.recyclerViewBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setUpBestDealsRv() {
        bestDealsAdapter = BestDealsAdapter()
        binding.recyclerViewBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun setUpSpecialProductsRecyclerView() {
        specialProductsAdaptor = SpecialProductsAdaptor()
        binding.recyclerViewSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdaptor
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}
package com.example.finalprojecte.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.finalprojecte.data.Categories
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.viewmodel.CategoryViewModel
import com.example.finalprojecte.viewmodel.factory.CategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class TelevisionFragment : FragmentBaseCategories() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> { CategoryViewModelFactory(firestore, Categories.Television) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        offerShowLoading()
                    }
                    is Resources.Success -> {
                        offerAdapter.differ.submitList(it.data)
                        offerHideLoading()
                    }
                    is Resources.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        offerHideLoading()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        bestProductsShowLoading()
                    }
                    is Resources.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        bestProductsHideLoading()
                    }
                    is Resources.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        bestProductsHideLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun offerPagingRequest() {
        viewModel.fetchOfferProducts()
    }

    override fun bestProdPagingRequest() {
        viewModel.fetchBestProducts()
    }
}
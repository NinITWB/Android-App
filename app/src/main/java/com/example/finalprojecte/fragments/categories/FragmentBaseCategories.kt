package com.example.finalprojecte.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojecte.R
import com.example.finalprojecte.adapters.BestProductsAdapter
import com.example.finalprojecte.databinding.FragmentBaseCategoriesBinding
import com.example.finalprojecte.utility.showBottomNavigation

open class FragmentBaseCategories : Fragment(R.layout.fragment_base_categories) {
    private lateinit var binding: FragmentBaseCategoriesBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoriesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOfferProducts()
        setUpBaseBestProducts()

        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_fragmentHome_to_fragmentProductDetails, b)
        }

        binding.recyclerViewStart.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    offerPagingRequest()
                }
            }
        })

        binding.nestedViewBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                bestProdPagingRequest()
            }
        })
    }

    open fun offerPagingRequest() {}

    open fun bestProdPagingRequest() {}

    fun offerShowLoading() {
        binding.offerProgressBar.visibility = View.VISIBLE
    }

    fun offerHideLoading() {
        binding.offerProgressBar.visibility = View.GONE
    }

    fun bestProductsShowLoading() {
        binding.bestBaseProductsProgressBar.visibility = View.VISIBLE
    }

    fun bestProductsHideLoading() {
        binding.bestBaseProductsProgressBar.visibility = View.GONE
    }

    private fun setUpBaseBestProducts() {
        binding.recyclerViewBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setUpOfferProducts() {
        binding.recyclerViewStart.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}
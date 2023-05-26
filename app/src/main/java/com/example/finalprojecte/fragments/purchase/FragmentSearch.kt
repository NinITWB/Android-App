package com.example.finalprojecte.fragments.purchase

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalprojecte.R
import com.example.finalprojecte.adapters.SearchAdapter
import com.example.finalprojecte.data.Products
import com.example.finalprojecte.databinding.FragmentSearchBinding
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.utility.showBottomNavigation
import com.example.finalprojecte.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import kotlin.collections.ArrayList

private val TAG = "FragmentSearch"
@AndroidEntryPoint
class FragmentSearch : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private val searchAdapter by lazy { SearchAdapter() }
    private val viewModel by viewModels<SearchViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSearchItem()
        lifecycleScope.launchWhenStarted {
            viewModel.searchItem.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        //showLoading()
                    }
                    is Resources.Success -> {
                        searchAdapter.differ.submitList(it.data)
                    }
                    is Resources.Error -> {
                        //hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        searchAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_fragmentSearch_to_fragmentProductDetails, b)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })
    }

    private fun filterList(query: String?) {
        if (query != null) {
            var filteredList = mutableListOf<Products>()

            for (i in viewModel.productData) {

                if (i?.name!!.toLowerCase(Locale.getDefault()).contains(query.lowercase())) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "No data found", Toast.LENGTH_LONG).show()
            } else {
                searchAdapter.differ.submitList(filteredList)
                searchAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setUpSearchItem() {
        binding.rvSearchBar.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}
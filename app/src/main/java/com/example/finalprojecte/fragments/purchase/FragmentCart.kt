package com.example.finalprojecte.fragments.purchase

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojecte.R
import com.example.finalprojecte.adapters.CartProductsAdapter
import com.example.finalprojecte.databinding.FragmentCartBinding
import com.example.finalprojecte.firebase.FireBaseFunction
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.utility.VerticalItemDecoration
import com.example.finalprojecte.utility.showBottomNavigation
import com.example.finalprojecte.viewmodel.ManageCartViewModel
import kotlinx.coroutines.flow.collectLatest
import java.text.DecimalFormat

class FragmentCart : Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductsAdapter() }
    private val viewModel by activityViewModels<ManageCartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCartProduct()

        var totalPrice = 0f
        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    totalPrice = it
                    binding.tvTotalPrice.text = "${convertCurrency(price)} VND"
                }
            }
        }

        binding.imageCloseCart.setOnClickListener {
            findNavController().navigateUp()
        }

        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product", it.products) }
            findNavController().navigate(R.id.action_fragmentCart_to_fragmentProductDetails, b)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FireBaseFunction.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FireBaseFunction.QuantityChanging.DECREASE)
        }

        binding.buttonCheckout.setOnClickListener {
            val action = FragmentCartDirections.actionFragmentCartToBillingFragment(totalPrice, cartAdapter.differ.currentList.toTypedArray())
            findNavController().navigate(action)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from your cart")
                    setMessage("Do you want to delete this item from your cart?")
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("Yes") { dialog, _ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()
                    }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when (it) {
                    is Resources.Loading -> {
                        binding.progressbarCart.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyCart()
                            hideOtherViews()
                        }
                        else {
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resources.Error -> {
                        binding.progressbarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "There're some errors: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun convertCurrency(num: Float) : String {
        val formatter = DecimalFormat("#,###");
        val formattedNumber = formatter.format(num);
        return formattedNumber
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            totalBoxContainer.visibility = View.GONE
            buttonCheckout.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    private fun setUpCartProduct() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}
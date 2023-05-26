package com.example.finalprojecte.fragments.purchase

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojecte.R
import com.example.finalprojecte.adapters.AddressAdapter
import com.example.finalprojecte.adapters.BillingProductsAdapter
import com.example.finalprojecte.data.Address
import com.example.finalprojecte.data.CartProducts
import com.example.finalprojecte.data.order.Order
import com.example.finalprojecte.data.order.OrderStatus
import com.example.finalprojecte.databinding.FragmentBillingBinding
import com.example.finalprojecte.utility.HorizontalItemDecoration
import com.example.finalprojecte.utility.Resources
import com.example.finalprojecte.viewmodel.BillingViewModel
import com.example.finalprojecte.viewmodel.OrderViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import org.json.JSONObject
import java.text.DecimalFormat

@AndroidEntryPoint
class BillingFragment : Fragment(R.layout.fragment_billing), PaymentResultListener {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private val args by navArgs<BillingFragmentArgs>()
    private var products = emptyList<CartProducts>()
    private var totalPrice = 0f

    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        products = args.products.toList()
        totalPrice = args.totalPrice
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("MissingInflatedId")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBillingProducts()
        setUpAddress()

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_fragmentAddress)
        }

        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonPayment.setOnClickListener {
            makePayment()
        }
        binding.buttonPayment.backgroundTintList = resources.getColorStateList(R.color.purple_500)

        lifecycleScope.launchWhenStarted {

            billingViewModel.address.collectLatest {
                when(it) {
                    is Resources.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resources.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resources.Error -> {
                        binding.progressbarAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), "Error ${it.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when(it) {
                    is Resources.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resources.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                    }
                    is Resources.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        billingAdapter.differ.submitList(products)
        binding.tvTotalPrice.text = "${convertCurrency(totalPrice)} VND"
        addressAdapter.onClick = {
            selectedAddress = it
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null) {
                val layout = layoutInflater.inflate(R.layout.custom_toast_success, null)
                val message = layout.findViewById<TextView>(R.id.toast_text_success)
                message.text = "You've placed products successfully"
                Toast(requireContext()).apply {
                    duration = Toast.LENGTH_LONG
                    setGravity(Gravity.TOP, 0, 0)
                    setView(layout)
                }.show()
                return@setOnClickListener
            }
            showOrderConfirmation()
        }
    }

    private fun makePayment() {
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","NinApp")
            options.put("description","Payment measure")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#3399cc")
            options.put("currency","INR")
            options.put("amount","50000")//pass amount in currency subunits



            val prefill = JSONObject()
            prefill.put("email","")
            prefill.put("contact","")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun convertCurrency(num: Float) : String {
        val formatter = DecimalFormat("#,###")
        val formattedNumber = formatter.format(num)
        return formattedNumber
    }

    private fun showOrderConfirmation() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order your cart items?")
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton("Yes") { dialog, _ ->
                val order = Order(
                    OrderStatus.Ordered.status,
                    totalPrice,
                    products,
                    selectedAddress!!
                )
                orderViewModel.placeOrder(order)
                dialog.dismiss()
            }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpAddress() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setUpBillingProducts() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPaymentSuccess(p0: String?) {

    }

    override fun onPaymentError(p0: Int, p1: String?) {
        TODO("Not yet implemented")
    }
}
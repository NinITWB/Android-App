package com.example.finalprojecte.fragments.purchase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.finalprojecte.R
import com.example.finalprojecte.adapters.HomeviewPagerAdaptor
import com.example.finalprojecte.databinding.FragmentHomeBinding
import com.example.finalprojecte.fragments.categories.*
import com.google.android.material.tabs.TabLayoutMediator

class FragmentHome : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragment = arrayListOf<Fragment>(
            FragmentMainCategories(),
            LaptopFragment(),
            CaseCPUFragment(),
            TelevisionFragment(),
            ElectronicDeviceFragment(),
            OthersFragment()
        )

        binding.viewPagerHome.isUserInputEnabled = false

        val viewPager2Adapter = HomeviewPagerAdaptor(categoriesFragment, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Laptop"
                2 -> tab.text = "CaseCPU"
                3 -> tab.text = "Television"
                4 -> tab.text = "Devices"
                5 -> tab.text = "Others"
            }
        }.attach()
    }
}
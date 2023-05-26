package com.example.finalprojecte.utility

import android.view.View
import androidx.fragment.app.Fragment
import com.example.finalprojecte.R
import com.example.finalprojecte.activities.ShoppingActivities
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.showBottomNavigation() {
    val bottomNavigation = (activity as ShoppingActivities).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigation.visibility = View.VISIBLE
}

fun Fragment.hideBottomNavigation() {
    val bottomNavigation = (activity as ShoppingActivities).findViewById<BottomNavigationView>(R.id.bottomNavigation)
    bottomNavigation.visibility = View.GONE
}
package com.example.finalprojecte.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.finalprojecte.data.Categories
import com.example.finalprojecte.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CategoryViewModelFactory(
    private val firestore: FirebaseFirestore,
    private val category: Categories
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore, category) as T
    }
}
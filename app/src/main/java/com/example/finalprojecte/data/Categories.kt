package com.example.finalprojecte.data

sealed class Categories(val category: String) {
    object Laptop : Categories("Laptop")
    object Television : Categories("Television")
    object CaseCPU : Categories("CaseCPU")
    object ElectronicDevice : Categories("Electronic device")
    object others : Categories("others")
}

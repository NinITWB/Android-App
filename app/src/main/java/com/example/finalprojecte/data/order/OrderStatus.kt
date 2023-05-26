package com.example.finalprojecte.data.order

sealed class OrderStatus(val status: String) {
    object Ordered: OrderStatus("Ordered")
    object Canceled: OrderStatus("Cancel")
    object Confirmed: OrderStatus("Confirmed")
    object Shipped: OrderStatus("Ship")
    object Returned: OrderStatus("Returned")
}

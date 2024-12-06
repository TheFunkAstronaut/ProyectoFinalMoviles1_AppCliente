package com.example.proyectofinalcliente.models

data class OrderItem(
    val id: Int,
    val quantity: Int,
    val price: String,
    val product: Product
)
package com.example.proyectofinalcliente.models

data class OrderRequest(
    val restaurant_id: Int,
    val total: Double,
    val address: String,
    val latitude: String,
    val longitude: String,
    val details: List<OrderDetail>,
    val status: Int = 0 // Estado inicial como "solicitado"
)

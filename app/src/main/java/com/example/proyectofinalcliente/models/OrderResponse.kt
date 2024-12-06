package com.example.proyectofinalcliente.models

data class OrderResponse(
    val id: Int,
    val user_id: Int,
    val restaurant_id: Int,
    val total: String,
    val latitude: String,
    val longitude: String,
    val address: String,
    val driver_id: Int?,
    val status: String,
    val created_at: String,
    val delivery_proof: String,
    val order_details: List<OrderItem>
)
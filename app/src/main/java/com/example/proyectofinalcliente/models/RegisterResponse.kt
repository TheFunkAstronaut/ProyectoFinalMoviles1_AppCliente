package com.example.proyectofinalcliente.models

data class RegisterResponse(
    val id: Int,
    val name: String,
    val email: String,
    val profile: Profile
)
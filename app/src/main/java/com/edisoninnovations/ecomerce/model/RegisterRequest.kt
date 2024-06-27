package com.edisoninnovations.ecomerce.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String,
    val address: String
)

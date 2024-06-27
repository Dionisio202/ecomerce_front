package com.edisoninnovations.ecomerce.model

data class AuthResponse(
    val token: String,
    val email: String,
    val role: String
)

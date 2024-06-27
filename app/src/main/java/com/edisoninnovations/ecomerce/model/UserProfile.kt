package com.edisoninnovations.ecomerce.model
data class UserProfile(
    val id: Int,
    val email: String,
    val name: String,
    val address: String,
    val role: String
)
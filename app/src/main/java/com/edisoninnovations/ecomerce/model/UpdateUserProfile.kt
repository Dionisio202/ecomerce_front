package com.edisoninnovations.ecomerce.model

data class UpdateUserProfile(
    val name: String?,
    val email: String?,
    val address: String?,
    val password: String? = null // Opcional si no se quiere actualizar la contraseña siempre
)
package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("product_id")
    val productId: Int = 0,
    val name: String,
    val price: Double,
    val stock: Int
)

package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName



data class Producto(
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Double,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("detalleCarritoPedido")
    val detalleCarritoPedido: List<DetalleCarritoCompra>? = null // Asegúrate de agregar esta línea
)

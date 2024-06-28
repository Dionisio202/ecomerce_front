package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName

data class DetallePedido(
    @SerializedName("order_detail_id")
    val orderDetailId: Int,
    @SerializedName("pedido")
    val pedido: Pedido, // Aquí es donde ocurre el problema, asegúrate de que sea un objeto Pedido
    @SerializedName("producto")
    val producto: Producto,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("discount_amount")
    val discountAmount: Double,
    @SerializedName("tax_amount")
    val taxAmount: Double
)


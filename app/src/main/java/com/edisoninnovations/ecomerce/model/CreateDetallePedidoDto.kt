package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName

data class CreateDetallePedidoDto(
    @SerializedName("pedido")
    var pedido: Int,
    @SerializedName("producto")
    val producto: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("price")
    val price: Double,
    @SerializedName("discount_amount")
    val discountAmount: Double,
    @SerializedName("tax_amount")
    val taxAmount: Double
)

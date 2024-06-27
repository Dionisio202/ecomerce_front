package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName

data class DetallePedido(
    @SerializedName("order_detail_id")
    val orderDetailId: Int,
    @SerializedName("pedido")
    val pedido: Int,
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

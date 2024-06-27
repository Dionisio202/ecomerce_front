package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName

data class Pedido(
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("cliente")
    val cliente: Int,
    @SerializedName("order_date")
    val orderDate: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("discount_amount")
    val discountAmount: Double,
    @SerializedName("tax_amount")
    val taxAmount: Double
)

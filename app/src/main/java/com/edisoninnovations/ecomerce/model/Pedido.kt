package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName

data class Pedido(
    @SerializedName("order_id")
    val orderId: Int,
    @SerializedName("cliente")
    val cliente: Cliente, // Cambia este campo para reflejar la estructura completa
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
    val taxAmount: Double,
    @SerializedName("detalles")
    val detalles: List<DetallePedido>
)

data class Cliente(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("pedidos")
    val pedidos: List<Pedido>,
    @SerializedName("carritos")
    val carritos: List<CarritoCompra>
)

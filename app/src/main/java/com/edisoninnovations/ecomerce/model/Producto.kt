package com.edisoninnovations.ecomerce.model

data class Producto(
    var product_id: Int,
    var name: String,
    var price: String,
    var stock: Int,
    var accounts: List<Any> = emptyList(),
    var detalleCarritoPedido: List<Any> = emptyList()
)

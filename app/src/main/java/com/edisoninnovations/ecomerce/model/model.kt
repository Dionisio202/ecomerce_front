package com.edisoninnovations.ecomerce.model

import com.google.gson.annotations.SerializedName

data class Categoria(
    @SerializedName("category_id")
    val categoryId: Int = 0,
    val name: String
)

data class Marca(
    @SerializedName("brand_id")
    val brandId: Int = 0,
    val name: String
)
data class CreateProductoRequest(
    val name: String,
    val price: Double,
    val stock: Int,
    @SerializedName("category")
    val categoryId: Int?,
    @SerializedName("brand")
    val brandId: Int?,
    val newCategory: Categoria? = null, // Añade valores por defecto null
    val newBrand: Marca? = null
)


data class ProductoCRUD(
    @SerializedName("product_id")
    val productId: Int = 0,
    val name: String,
    val price: Double,
    val stock: Int,
    val category: Categoria,
    val brand: Marca
)


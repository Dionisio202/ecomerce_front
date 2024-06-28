package com.edisoninnovations.ecomerce.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Categoria(
    @SerializedName("category_id")
    val categoryId: Int = 0,
    val name: String,
    val productos: List<Producto>? = emptyList()
)
data class Marca(
    @SerializedName("brand_id")
    val brandId: Int = 0,
    val name: String,
    val productos: List<Producto>? = emptyList()
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

data class EditProductoRequest(
    val name: String,
    val price: Double,
    val stock: Int,

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
data class CreateCarritoRequest(
    val cliente: Int
)

data class CreateDetalleCarritoCompraDto(
    val carrito: Int,
    val producto: Int,
    val quantity: Int,
    val unit_price: Double
)

data class CarritoCompra(
    @SerializedName("cart_id")
    val cartId: Int,
    @SerializedName("detalles")
    val detalles: List<DetalleCarritoCompra> = emptyList()
)
data class User(
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

data class DetalleCarritoCompra(
    @SerializedName("detail_id")
    val detailId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: Double,
    @SerializedName("producto")
    val producto: Int // Este es el ID del producto
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(detailId)
        parcel.writeInt(quantity)
        parcel.writeDouble(unitPrice)
        parcel.writeInt(producto)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<DetalleCarritoCompra> {
        override fun createFromParcel(parcel: Parcel): DetalleCarritoCompra {
            return DetalleCarritoCompra(parcel)
        }

        override fun newArray(size: Int): Array<DetalleCarritoCompra?> {
            return arrayOfNulls(size)
        }
    }
}
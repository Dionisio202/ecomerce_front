package com.edisoninnovations.ecomerce.request

import com.edisoninnovations.ecomerce.model.AuthResponse
import com.edisoninnovations.ecomerce.model.CarritoCompra
import com.edisoninnovations.ecomerce.model.Categoria
import com.edisoninnovations.ecomerce.model.CreateCarritoRequest
import com.edisoninnovations.ecomerce.model.CreateDetalleCarritoCompraDto
import com.edisoninnovations.ecomerce.model.CreateProductoRequest
import com.edisoninnovations.ecomerce.model.DetalleCarritoCompra
import com.edisoninnovations.ecomerce.model.LoginRequest
import com.edisoninnovations.ecomerce.model.Marca
import com.edisoninnovations.ecomerce.model.Producto
import com.edisoninnovations.ecomerce.model.ProductoCRUD
import com.edisoninnovations.ecomerce.model.RegisterRequest
import com.edisoninnovations.ecomerce.model.UpdateUserProfile
import com.edisoninnovations.ecomerce.model.User
import com.edisoninnovations.ecomerce.model.UserProfile
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): AuthResponse

    @POST("/users")
    suspend fun register(@Body registerRequest: RegisterRequest): AuthResponse

    @GET("auth/profile")
    suspend fun getProfile(@Header("Authorization") token: String): UserProfile

    @PATCH("users/{id}")
    suspend fun updateUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body userProfile: UpdateUserProfile
    ): UserProfile

    @GET("productos")
    suspend fun getProducts(): List<Producto>

    @POST("productos")
    suspend fun addProduct(@Body producto: CreateProductoRequest): ProductoCRUD

    @DELETE("productos/{id}")
    suspend fun deleteProduct(@Path("id") id: Int)

    @GET("categorias")
    suspend fun getCategories(): List<Categoria>


    @GET("marcas")
    suspend fun getBrands(): List<Marca>

    @PATCH("productos/{id}")
    suspend fun patchProduct(
        @Path("id") id: Int,
        @Body producto: CreateProductoRequest
    ): ProductoCRUD
    @POST("carrito-compras")
    suspend fun addCart(@Body carrito: CreateCarritoRequest): CarritoCompra

    @POST("detalle-carrito-compras")
    suspend fun addCartDetail(@Body detail: CreateDetalleCarritoCompraDto): DetalleCarritoCompra


    @GET("carrito-compras/{cartId}")
    suspend fun getCartItems(@Path("cartId") cartId: Int): CarritoCompra
    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: Int): User
    @GET("productos/{productId}")
    suspend fun getProductById(@Path("productId") productId: Int): Producto
}

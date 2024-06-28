package com.edisoninnovations.ecomerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.edisoninnovations.ecomerce.databinding.ItemProductBinding
import com.edisoninnovations.ecomerce.model.CreateCarritoRequest
import com.edisoninnovations.ecomerce.model.CreateDetalleCarritoCompraDto
import com.edisoninnovations.ecomerce.model.Producto
import com.edisoninnovations.ecomerce.model.User
import com.edisoninnovations.ecomerce.request.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductAdapter(private val context: Context, private val products: List<Producto>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val role = sharedPreferences.getString("role", "user")
    private val userId = sharedPreferences.getInt("user_id", -1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Producto) {
            binding.txtMarca.text = product.name
            binding.txtModelo.text = "Price: ${product.price}"
            binding.txtAnio.text = "Stock: ${product.stock}"

            if (role == "admin") {
                binding.imageView2.visibility = View.GONE
            } else {
                binding.imageView2.visibility = View.VISIBLE
                binding.imageView2.setOnClickListener {
                    if (product.stock > 0) {
                        showConfirmationDialog(product)
                    } else {
                        Toast.makeText(context, "Stock insuficiente. No se puede añadir al carrito.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun showConfirmationDialog(product: Producto) {
            AlertDialog.Builder(context).apply {
                setTitle("Confirmación")
                setMessage("¿Desea añadir ${product.name} al carrito?")
                setPositiveButton("Sí") { _, _ -> addToCart(product) }
                setNegativeButton("No", null)
                show()
            }
        }

        private fun addToCart(product: Producto) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val user = RetrofitClient.instance.getUserById(userId)
                    val carritoId = getOrCreateCart(user)
                    if (carritoId != null) {
                        val detailRequest = CreateDetalleCarritoCompraDto(
                            carrito = carritoId,
                            producto = product.productId,
                            quantity = 1,
                            unit_price = product.price
                        )
                        RetrofitClient.instance.addCartDetail(detailRequest)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Añadido al carrito", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error al crear o obtener el carrito", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Añadido al carrito", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private suspend fun getOrCreateCart(user: User): Int? {
            return try {
                if (user.carritos.isNotEmpty()) {
                    user.carritos.first().cartId
                } else {
                    val newCart = RetrofitClient.instance.addCart(CreateCarritoRequest(user.id))
                    newCart.cartId
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}

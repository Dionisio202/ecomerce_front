package com.edisoninnovations.ecomerce.ProductAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edisoninnovations.ecomerce.databinding.ItemProductBinding
import com.edisoninnovations.ecomerce.model.Producto

class ProductAdapter(private val context: Context, private val products: List<Producto>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val role = sharedPreferences.getString("role", "user")

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

            // Ocultar el ImageView si el rol es admin
            if (role == "admin") {
                binding.imageView2.visibility = View.GONE
            } else {
                binding.imageView2.visibility = View.VISIBLE
            }
        }
    }
}

package com.edisoninnovations.ecomerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edisoninnovations.ecomerce.databinding.ItemCartBinding
import com.edisoninnovations.ecomerce.model.DetalleCarritoCompra

class CartAdapter(
    private val context: Context,
    private val cartItems: List<DetalleCarritoCompra>,
    private val onDetailClick: (DetalleCarritoCompra) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: DetalleCarritoCompra) {
            binding.txtProductName.text = "Detalle de Compra #${cartItem.detailId}"
            binding.txtProductPrice.text = "Price: ${cartItem.unitPrice}"
            binding.txtProductQuantity.text = "Quantity: ${cartItem.quantity}"

            itemView.setOnClickListener {
                onDetailClick(cartItem)
            }
        }
    }
}

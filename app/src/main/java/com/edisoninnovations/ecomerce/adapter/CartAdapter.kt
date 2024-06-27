package com.edisoninnovations.ecomerce.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edisoninnovations.ecomerce.databinding.ItemCartBinding
import com.edisoninnovations.ecomerce.model.DetalleCarritoCompra
import com.edisoninnovations.ecomerce.model.Producto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartAdapter(
    private val context: Context,
    private val cartItems: MutableList<DetalleCarritoCompra>,
    private val getProductDetailsByDetailId: suspend (Int) -> Producto?,
    private val onDeleteClick: (DetalleCarritoCompra) -> Unit,
    private val onPayClick: (DetalleCarritoCompra) -> Unit
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
            CoroutineScope(Dispatchers.Main).launch {
                val product = getProductDetailsByDetailId(cartItem.detailId)
                binding.txtProductName.text = product?.name ?: "Unknown Product"
                binding.txtProductPrice.text = "Price: ${cartItem.unitPrice}"
                binding.txtProductQuantity.text = "Quantity: ${cartItem.quantity}"
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(cartItem)
            }

            binding.btnPay.setOnClickListener {
                onPayClick(cartItem)
            }
        }
    }

    fun removeItem(position: Int) {
        cartItems.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }
}

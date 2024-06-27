package com.edisoninnovations.ecomerce.ui.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.edisoninnovations.ecomerce.OrderActivity
import com.edisoninnovations.ecomerce.adapter.CartAdapter
import com.edisoninnovations.ecomerce.databinding.FragmentGalleryBinding
import com.edisoninnovations.ecomerce.model.DetalleCarritoCompra
import com.edisoninnovations.ecomerce.model.Producto
import com.edisoninnovations.ecomerce.request.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<DetalleCarritoCompra>()
    private val userId by lazy {
        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getInt("user_id", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cartAdapter = CartAdapter(requireContext(), cartItems, ::getProductDetailsByDetailId, ::onDeleteClick, ::onPayClick)
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCart.adapter = cartAdapter

        loadCartItems()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadCartItems() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = RetrofitClient.instance.getUserById(userId)
                val allDetails = mutableListOf<DetalleCarritoCompra>()
                user.carritos.forEach { carrito ->
                    allDetails.addAll(carrito.detalles)
                }
                cartItems.clear()
                cartItems.addAll(allDetails)
                cartAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load cart items", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getProductDetailsByDetailId(detailId: Int): Producto? {
        val products = RetrofitClient.instance.getProducts()
        products.forEach { product ->
            product.detalleCarritoPedido?.find { it.detailId == detailId }?.let {
                return product
            }
        }
        return null
    }

    private fun onDeleteClick(cartItem: DetalleCarritoCompra) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.instance.deleteCartItem(cartItem.detailId)
                if (response.isSuccessful) {
                    val position = cartItems.indexOf(cartItem)
                    if (position != -1) {
                        cartAdapter.removeItem(position)
                        Toast.makeText(context, "Producto eliminado del carrito", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al eliminar el producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onPayClick(cartItem: DetalleCarritoCompra) {
        val intent = Intent(context, OrderActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("cartItem", cartItem)
        }
        startActivity(intent)
    }

}

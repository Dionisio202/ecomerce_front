package com.edisoninnovations.ecomerce.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.edisoninnovations.ecomerce.adapter.OrderAdapter
import com.edisoninnovations.ecomerce.databinding.FragmentSlideshowBinding
import com.edisoninnovations.ecomerce.model.Pedido
import com.edisoninnovations.ecomerce.request.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: OrderAdapter
    private val userId by lazy {
        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getInt("user_id", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(context)

        loadOrders()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadOrders() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = RetrofitClient.instance.getUserById(userId)
                val orders = user.pedidos
                orderAdapter = OrderAdapter(orders)
                binding.recyclerViewOrders.adapter = orderAdapter
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

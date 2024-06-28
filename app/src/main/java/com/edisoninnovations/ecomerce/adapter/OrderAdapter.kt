package com.edisoninnovations.ecomerce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edisoninnovations.ecomerce.databinding.ItemOrderBinding
import com.edisoninnovations.ecomerce.model.Pedido
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private val orders: List<Pedido>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Pedido) {
            binding.txtOrderDate.text = formatDate(order.orderDate)
            binding.txtOrderStatus.text = order.status
            binding.txtOrderPaymentMethod.text = order.paymentMethod
            binding.txtOrderTotalAmount.text = "Total: ${order.totalAmount}"
        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)
            val outputDateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return "${outputDateFormat.format(date)} a las ${outputTimeFormat.format(date)}"
        }
    }
}

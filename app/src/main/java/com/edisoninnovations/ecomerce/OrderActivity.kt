package com.edisoninnovations.ecomerce

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.model.CreateDetallePedidoDto
import com.edisoninnovations.ecomerce.model.CreatePedidoDto
import com.edisoninnovations.ecomerce.model.DetalleCarritoCompra
import com.edisoninnovations.ecomerce.request.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderActivity : AppCompatActivity() {

    private lateinit var edtCliente: EditText
    private lateinit var edtOrderDate: EditText
    private lateinit var edtStatus: EditText
    private lateinit var edtPaymentMethod: EditText
    private lateinit var edtTotalAmount: EditText
    private lateinit var edtDiscountAmount: EditText
    private lateinit var edtTaxAmount: EditText

    private lateinit var edtProducto: EditText
    private lateinit var edtQuantity: EditText
    private lateinit var edtPrice: EditText
    private lateinit var edtDiscount: EditText
    private lateinit var edtTax: EditText

    private lateinit var btnRegisterPayment: Button
    private var userId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        edtCliente = findViewById(R.id.edtCliente)
        edtOrderDate = findViewById(R.id.edtOrderDate)
        edtStatus = findViewById(R.id.edtStatus)
        edtPaymentMethod = findViewById(R.id.edtPaymentMethod)
        edtTotalAmount = findViewById(R.id.edtTotalAmount)
        edtDiscountAmount = findViewById(R.id.edtDiscountAmount)
        edtTaxAmount = findViewById(R.id.edtTaxAmount)

        edtProducto = findViewById(R.id.edtProducto)
        edtQuantity = findViewById(R.id.edtQuantity)
        edtPrice = findViewById(R.id.edtPrice)
        edtDiscount = findViewById(R.id.edtDiscount)
        edtTax = findViewById(R.id.edtTax)

        btnRegisterPayment = findViewById(R.id.btnRegisterPayment)

        userId = intent.getIntExtra("userId", 0)
        val cartItem = intent.getParcelableExtra<DetalleCarritoCompra>("cartItem")

        edtCliente.setText(userId.toString())

        cartItem?.let {
            edtProducto.setText(it.producto.toString())
            edtQuantity.setText(it.quantity.toString())
            edtPrice.setText(it.unitPrice.toString())
            // Fill other fields as necessary
        }

        btnRegisterPayment.setOnClickListener {
            if (validateInputs()) {
                registerPayment()
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        return edtOrderDate.text.isNotEmpty() &&
                edtStatus.text.isNotEmpty() &&
                edtPaymentMethod.text.isNotEmpty() &&
                edtTotalAmount.text.isNotEmpty() &&
                edtDiscountAmount.text.isNotEmpty() &&
                edtTaxAmount.text.isNotEmpty() &&
                edtProducto.text.isNotEmpty() &&
                edtQuantity.text.isNotEmpty() &&
                edtPrice.text.isNotEmpty() &&
                edtDiscount.text.isNotEmpty() &&
                edtTax.text.isNotEmpty()
    }

    private fun registerPayment() {
        val pedido = CreatePedidoDto(
            cliente = userId,
            orderDate = edtOrderDate.text.toString(),
            status = edtStatus.text.toString(),
            paymentMethod = edtPaymentMethod.text.toString(),
            totalAmount = edtTotalAmount.text.toString().toDouble(),
            discountAmount = edtDiscountAmount.text.toString().toDouble(),
            taxAmount = edtTaxAmount.text.toString().toDouble()
        )

        val detallePedido = CreateDetallePedidoDto(
            pedido = 0, // This will be updated after creating the pedido
            producto = edtProducto.text.toString().toInt(),
            quantity = edtQuantity.text.toString().toInt(),
            price = edtPrice.text.toString().toDouble(),
            discountAmount = edtDiscount.text.toString().toDouble(),
            taxAmount = edtTax.text.toString().toDouble()
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val pedidoResponse = RetrofitClient.instance.createPedido(pedido)
                detallePedido.pedido = pedidoResponse.orderId // Update the order ID
                val detallePedidoResponse = RetrofitClient.instance.createDetallePedido(detallePedido)
                Toast.makeText(this@OrderActivity, "Pago registrado exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@OrderActivity, MainActivity::class.java)
                intent.putExtra("navigateTo", "HomeFragment")
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this@OrderActivity, "Pago registrado exitosamente", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@OrderActivity, MainActivity::class.java)
                intent.putExtra("navigateTo", "HomeFragment")
                startActivity(intent)
            }
        }
    }
}

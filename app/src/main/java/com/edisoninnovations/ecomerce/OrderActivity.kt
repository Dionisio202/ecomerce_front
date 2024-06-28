package com.edisoninnovations.ecomerce
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.model.CreateDetallePedidoDto
import com.edisoninnovations.ecomerce.model.CreatePedidoDto
import com.edisoninnovations.ecomerce.model.DetalleCarritoCompra
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.edisoninnovations.ecomerce.utils.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderActivity : AppCompatActivity() {

    private lateinit var edtOrderDate: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var edtQuantity: EditText
    private lateinit var edtPrice: EditText
    private lateinit var btnRegisterPayment: Button
    private var userId: Int = 0
    private var productId: Int = 0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        loadingDialog = LoadingDialog(this)
        // Initialize all the views
        edtOrderDate = findViewById(R.id.edtOrderDate)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod)
        edtQuantity = findViewById(R.id.edtQuantity)
        edtPrice = findViewById(R.id.edtPrice)
        btnRegisterPayment = findViewById(R.id.btnRegisterPayment)

        // Get data from Intent
        userId = intent.getIntExtra("userId", 0)
        productId = intent.getIntExtra("productId", 0)
        val cartItem = intent.getParcelableExtra<DetalleCarritoCompra>("cartItem")

        // Set the quantity and price from cart item
        cartItem?.let {
            edtQuantity.setText(it.quantity.toString())
            edtPrice.setText(it.unitPrice.toString())
        }

        // Setup DatePicker
        edtOrderDate.setOnClickListener {
            showDatePickerDialog()
        }

        // Setup Spinners
        val statusAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.status_options,
            android.R.layout.simple_spinner_item
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter

        val paymentMethodAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.payment_methods,
            android.R.layout.simple_spinner_item
        )
        paymentMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = paymentMethodAdapter

        btnRegisterPayment.setOnClickListener {
            if (validateInputs()) {
                registerPayment()
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                edtOrderDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun validateInputs(): Boolean {
        return edtOrderDate.text.isNotEmpty() &&
                spinnerStatus.selectedItemPosition != 0 &&
                spinnerPaymentMethod.selectedItemPosition != 0 &&
                edtQuantity.text.isNotEmpty() &&
                edtPrice.text.isNotEmpty()
    }

    private fun calculateAmounts(): Pair<Double, Double> {
        val quantity = edtQuantity.text.toString().toIntOrNull() ?: 0
        val price = edtPrice.text.toString().toDoubleOrNull() ?: 0.0
        val totalAmount = quantity * price
        val taxAmount = totalAmount * 0.05
        return Pair(totalAmount, taxAmount)
    }

    private fun registerPayment() {
        loadingDialog.startLoading()
        val (totalAmount, taxAmount) = calculateAmounts()
        val discountAmount = 0.0 // Si tienes lógica para descuentos, aplícala aquí

        val pedido = CreatePedidoDto(
            cliente = userId,
            orderDate = edtOrderDate.text.toString(),
            status = spinnerStatus.selectedItem.toString(),
            paymentMethod = spinnerPaymentMethod.selectedItem.toString(),
            totalAmount = totalAmount + taxAmount,
            discountAmount = discountAmount,
            taxAmount = taxAmount
        )

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val pedidoResponse = RetrofitClient.instance.createPedido(pedido)
                val detallePedido = CreateDetallePedidoDto(
                    pedido = pedidoResponse.orderId, // Update the order ID
                    producto = productId,
                    quantity = edtQuantity.text.toString().toInt(),
                    price = edtPrice.text.toString().toDouble(),
                    discountAmount = discountAmount,
                    taxAmount = taxAmount
                )
                val detallePedidoResponse = RetrofitClient.instance.createDetallePedido(detallePedido)
                Toast.makeText(this@OrderActivity, "Pago registrado exitosamente", Toast.LENGTH_SHORT).show()
                loadingDialog.isDismiss()
                val intent = Intent(this@OrderActivity, MainActivity::class.java)
                intent.putExtra("navigateTo", "HomeFragment")
                startActivity(intent)
            } catch (e: Exception) {
                loadingDialog.isDismiss()
                e.printStackTrace()
                Toast.makeText(this@OrderActivity, "Error al registrar el pago", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

abstract class SimpleTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {}
}

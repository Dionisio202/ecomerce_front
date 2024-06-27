package com.edisoninnovations.ecomerce.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.model.Producto
import com.edisoninnovations.ecomerce.request.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProductFragment : Fragment() {

    private var productId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_product, container, false)

        val editTextProductName = view.findViewById<EditText>(R.id.editTextProductName)
        val editTextProductPrice = view.findViewById<EditText>(R.id.editTextProductPrice)
        val editTextProductStock = view.findViewById<EditText>(R.id.editTextProductStock)
        val buttonEditProduct = view.findViewById<Button>(R.id.buttonEditProduct)

        // Set productId from arguments (Assume you pass the productId as an argument)
        productId = arguments?.getInt("product_id")

        buttonEditProduct.setOnClickListener {
            val productName = editTextProductName.text.toString()
            val productPrice = editTextProductPrice.text.toString().toDouble()
            val productStock = editTextProductStock.text.toString().toInt()

//            val updatedProduct = Producto(
//                product_id = productId!!,
//                name = productName,
//                price = productPrice,
//                stock = productStock,
//                category = /* tu objeto Categoria */,
//                brand = /* tu objeto Marca */
//            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
//                    val response = RetrofitClient.instance.updateProduct(productId!!, updatedProduct)
                    Toast.makeText(context, "Product updated successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to update product", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}

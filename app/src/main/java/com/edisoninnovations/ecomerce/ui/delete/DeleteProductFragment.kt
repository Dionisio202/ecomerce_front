package com.edisoninnovations.ecomerce.ui.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.request.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeleteProductFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_delete_product, container, false)

        val editTextProductId = view.findViewById<EditText>(R.id.editTextProductId)
        val buttonDeleteProduct = view.findViewById<Button>(R.id.buttonDeleteProduct)

        buttonDeleteProduct.setOnClickListener {
            val productId = editTextProductId.text.toString().toInt()

            CoroutineScope(Dispatchers.Main).launch {
                try {
//                    val response = RetrofitClient.instance.deleteProduct(productId)
                    Toast.makeText(context, "Product deleted successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}

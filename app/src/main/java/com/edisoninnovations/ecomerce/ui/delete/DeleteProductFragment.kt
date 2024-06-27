package com.edisoninnovations.ecomerce.ui.delete

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.model.Producto
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.edisoninnovations.ecomerce.ui.CustomSpinnerE
import com.edisoninnovations.ecomerce.utils.FragmentLoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeleteProductFragment : Fragment() {

    private lateinit var productSpinner: Spinner
    private lateinit var loadingDialog: FragmentLoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_delete_product, container, false)
        productSpinner = view.findViewById(R.id.productSpinner)
        val buttonDeleteProduct = view.findViewById<Button>(R.id.buttonDeleteProduct)
        loadingDialog = FragmentLoadingDialog(requireActivity())

        loadProducts()

        buttonDeleteProduct.setOnClickListener {
            val selectedProduct = productSpinner.selectedItem as Producto
            deleteProduct(selectedProduct.productId)
        }

        return view
    }

    private fun loadProducts() {
        loadingDialog.startLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val products = RetrofitClient.instance.getProducts()
                withContext(Dispatchers.Main) {
                    productSpinner.adapter = CustomSpinnerE(requireContext(), products)
                    loadingDialog.isDismiss()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al cargar productos: ${e.message}", Toast.LENGTH_SHORT).show()
                    loadingDialog.isDismiss()
                }
            }
        }
    }

    private fun deleteProduct(productId: Int) {
        loadingDialog.startLoading()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.instance.deleteProduct(productId)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    loadingDialog.isDismiss() // Cerrar el diálogo antes de navegar
                    findNavController().navigate(R.id.nav_home) // Navegar de regreso al HomeFragment
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al eliminar producto: ${e.message}", Toast.LENGTH_SHORT).show()
                    loadingDialog.isDismiss()
                }
            }
        }
    }
}

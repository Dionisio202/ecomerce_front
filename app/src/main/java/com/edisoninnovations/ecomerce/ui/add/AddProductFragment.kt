package com.edisoninnovations.ecomerce.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.model.Categoria
import com.edisoninnovations.ecomerce.model.CreateProductoRequest
import com.edisoninnovations.ecomerce.model.Marca
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.edisoninnovations.ecomerce.ui.CustomSpinnerAdapter
import com.edisoninnovations.ecomerce.utils.FragmentLoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {

    private lateinit var categorySpinner: Spinner
    private lateinit var brandSpinner: Spinner
    private lateinit var loadingDialog: FragmentLoadingDialog
    private lateinit var viewRoot: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewRoot = inflater.inflate(R.layout.fragment_add_product, container, false)
        loadingDialog = FragmentLoadingDialog(requireActivity())

        setUpViews(viewRoot)
        setUpListeners(viewRoot)
        loadCategoriesAndBrands()

        return viewRoot
    }

    private fun setUpViews(view: View) {
        categorySpinner = view.findViewById(R.id.categorySpinner)
        brandSpinner = view.findViewById(R.id.brandSpinner)
        view.findViewById<EditText>(R.id.editTextProductName).addTextChangedListener(textWatcher)
        view.findViewById<EditText>(R.id.editTextProductPrice).addTextChangedListener(textWatcher)
        view.findViewById<EditText>(R.id.editTextProductStock).addTextChangedListener(textWatcher)
    }

    private fun setUpListeners(view: View) {
        val buttonAddProduct = view.findViewById<Button>(R.id.buttonAddProduct)
        buttonAddProduct.isEnabled = false // Initial state
        buttonAddProduct.setOnClickListener {
            addProduct(view)
        }
    }

    private fun addProduct(view: View) {
        val productName = view.findViewById<EditText>(R.id.editTextProductName).text.toString()
        val productPrice = view.findViewById<EditText>(R.id.editTextProductPrice).text.toString().toDouble()
        val productStock = view.findViewById<EditText>(R.id.editTextProductStock).text.toString().toInt()
        val selectedCategory = categorySpinner.selectedItem as Categoria
        val selectedBrand = brandSpinner.selectedItem as Marca

        val newProductRequest = CreateProductoRequest(
            name = productName,
            price = productPrice,
            stock = productStock,
            categoryId = selectedCategory.categoryId,
            brandId = selectedBrand.brandId
        )

        loadingDialog.startLoading() // Muestra el diálogo de carga
        CoroutineScope(Dispatchers.Main).launch {
            try {
                RetrofitClient.instance.addProduct(newProductRequest).let {
                    loadingDialog.isDismiss() // Cierra el diálogo de carga
                    Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                loadingDialog.isDismiss() // Asegura que el diálogo se cierra en caso de error
                Toast.makeText(context, "Error al crear producto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategoriesAndBrands() {
        loadingDialog.startLoading() // Muestra el diálogo de carga
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val categories = RetrofitClient.instance.getCategories()
                val brands = RetrofitClient.instance.getBrands()

                categorySpinner.adapter = CustomSpinnerAdapter(requireContext(), categories)
                brandSpinner.adapter = CustomSpinnerAdapter(requireContext(), brands)
                loadingDialog.isDismiss() // Cierra el diálogo de carga
            } catch (e: Exception) {
                loadingDialog.isDismiss() // Asegura que el diálogo se cierra en caso de error
                Toast.makeText(context, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            validateForm()
        }

        private fun validateForm() {
            val productName = viewRoot.findViewById<EditText>(R.id.editTextProductName).text.toString()
            val productPrice = viewRoot.findViewById<EditText>(R.id.editTextProductPrice).text.toString()
            val productStock = viewRoot.findViewById<EditText>(R.id.editTextProductStock).text.toString()

            val isProductNameValid = productName.isNotEmpty()
            val isProductPriceValid = productPrice.isNotEmpty() && productPrice.toDoubleOrNull()?.let { it > 0 } == true
            val isProductStockValid = productStock.isNotEmpty() && productStock.toIntOrNull()?.let { it > 0 } == true

            viewRoot.findViewById<Button>(R.id.buttonAddProduct).isEnabled = isProductNameValid && isProductPriceValid && isProductStockValid
        }
    }
}

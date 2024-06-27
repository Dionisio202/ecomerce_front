package com.edisoninnovations.ecomerce.ui.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.model.Categoria
import com.edisoninnovations.ecomerce.model.CreateProductoRequest
import com.edisoninnovations.ecomerce.model.Marca
import com.edisoninnovations.ecomerce.model.Producto
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.edisoninnovations.ecomerce.ui.CustomSpinnerAdapter
import com.edisoninnovations.ecomerce.utils.FragmentLoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProductFragment : Fragment() {

    private lateinit var productSpinner: Spinner
    private lateinit var categorySpinner: Spinner
    private lateinit var brandSpinner: Spinner
    private lateinit var loadingDialog: FragmentLoadingDialog
    private lateinit var viewRoot: View
    private lateinit var originalProduct: Producto

    private var categories: List<Categoria> = emptyList()
    private var brands: List<Marca> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewRoot = inflater.inflate(R.layout.fragment_edit_product, container, false)
        loadingDialog = FragmentLoadingDialog(requireActivity())

        setUpViews(viewRoot)
        setUpListeners(viewRoot)
        loadProducts()
        loadCategoriesAndBrands()

        return viewRoot
    }

    private fun setUpViews(view: View) {
        productSpinner = view.findViewById(R.id.productSpinner)
        categorySpinner = view.findViewById(R.id.categorySpinner)
        brandSpinner = view.findViewById(R.id.brandSpinner)
        view.findViewById<EditText>(R.id.editTextProductName).addTextChangedListener(textWatcher)
        view.findViewById<EditText>(R.id.editTextProductPrice).addTextChangedListener(textWatcher)
        view.findViewById<EditText>(R.id.editTextProductStock).addTextChangedListener(textWatcher)
    }

    private fun setUpListeners(view: View) {
        productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedProduct = parent.getItemAtPosition(position) as Producto
                originalProduct = selectedProduct
                Log.d("EditProductFragment", "Selected product: $selectedProduct")
                populateProductDetails(selectedProduct)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        val buttonEditProduct = view.findViewById<Button>(R.id.buttonEditProduct)
        buttonEditProduct.isEnabled = false // Initial state
        buttonEditProduct.setOnClickListener {
            editProduct(view)
        }
    }

    private fun populateProductDetails(product: Producto) {
        Log.d("EditProductFragment", "Populating product details for: $product")
        viewRoot.findViewById<EditText>(R.id.editTextProductName).setText(product.name)
        viewRoot.findViewById<EditText>(R.id.editTextProductPrice).setText(product.price.toString())
        viewRoot.findViewById<EditText>(R.id.editTextProductStock).setText(product.stock.toString())

        // Set the category and brand spinners to the correct values
        val categoryAdapter = categorySpinner.adapter as? CustomSpinnerAdapter<Categoria>
        val brandAdapter = brandSpinner.adapter as? CustomSpinnerAdapter<Marca>

        val category = categories.find { it.productos?.any { p -> p.productId == product.productId } == true }
        category?.let {
            val categoryPosition = categoryAdapter?.getCustomPosition(it) ?: -1
            Log.d("EditProductFragment", "Category position: $categoryPosition for category: $it")
            if (categoryPosition != -1) categorySpinner.setSelection(categoryPosition)
        }

        val brand = brands.find { it.productos?.any { p -> p.productId == product.productId } == true }
        brand?.let {
            val brandPosition = brandAdapter?.getCustomPosition(it) ?: -1
            Log.d("EditProductFragment", "Brand position: $brandPosition for brand: $it")
            if (brandPosition != -1) brandSpinner.setSelection(brandPosition)
        }
    }

    private fun editProduct(view: View) {
        val productName = view.findViewById<EditText>(R.id.editTextProductName).text.toString()
        val productPrice = view.findViewById<EditText>(R.id.editTextProductPrice).text.toString().toDouble()
        val productStock = view.findViewById<EditText>(R.id.editTextProductStock).text.toString().toInt()
        val selectedCategory = categorySpinner.selectedItem as Categoria
        val selectedBrand = brandSpinner.selectedItem as Marca
        val selectedProduct = productSpinner.selectedItem as Producto

        val updatedProductRequest = CreateProductoRequest(
            name = productName,
            price = productPrice,
            stock = productStock,
            categoryId = selectedCategory.categoryId,
            brandId = selectedBrand.brandId
        )

        Log.d("EditProductFragment", "Updating product with ID: ${selectedProduct.productId}")

        loadingDialog.startLoading() // Muestra el diálogo de carga
        CoroutineScope(Dispatchers.Main).launch {
            try {
                RetrofitClient.instance.patchProduct(selectedProduct.productId, updatedProductRequest).let {
                    Toast.makeText(context, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show()
                    withContext(Dispatchers.Main) {
                        loadingDialog.isDismiss() // Asegura que el diálogo se cierra
                        findNavController().navigate(R.id.nav_home) // Navegar de regreso al HomeFragment
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    loadingDialog.isDismiss() // Asegura que el diálogo se cierra siempre en caso de error
                    Toast.makeText(context, "Error al actualizar producto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadProducts() {
        loadingDialog.startLoading() // Muestra el diálogo de carga
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val products = RetrofitClient.instance.getProducts()
                withContext(Dispatchers.Main) {
                    productSpinner.adapter = CustomSpinnerAdapter(requireContext(), products)
                    Log.d("EditProductFragment", "Productos cargados: ${products.size}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al cargar productos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    loadingDialog.isDismiss() // Asegura que el diálogo se cierra siempre
                }
            }
        }
    }

    private fun loadCategoriesAndBrands() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val categoriesResult = RetrofitClient.instance.getCategories()
                val brandsResult = RetrofitClient.instance.getBrands()

                withContext(Dispatchers.Main) {
                    categories = categoriesResult
                    brands = brandsResult

                    Log.d("EditProductFragment", "Categorías cargadas: ${categories.size}")
                    Log.d("EditProductFragment", "Marcas cargadas: ${brands.size}")

                    categorySpinner.adapter = CustomSpinnerAdapter(requireContext(), categories)
                    brandSpinner.adapter = CustomSpinnerAdapter(requireContext(), brands)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    loadingDialog.isDismiss() // Asegura que el diálogo se cierra siempre
                }
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

            val isChanged = productName != originalProduct.name ||
                    productPrice != originalProduct.price.toString() ||
                    productStock != originalProduct.stock.toString() ||
                    categorySpinner.selectedItem != categories.find { it.productos?.any { p -> p.productId == originalProduct.productId } == true } ||
                    brandSpinner.selectedItem != brands.find { it.productos?.any { p -> p.productId == originalProduct.productId } == true }

            viewRoot.findViewById<Button>(R.id.buttonEditProduct).isEnabled = isProductNameValid && isProductPriceValid && isProductStockValid && isChanged
        }
    }
}

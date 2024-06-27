package com.edisoninnovations.ecomerce.ui.add

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.edisoninnovations.ecomerce.ui.manage.ManageBrandsActivity
import com.edisoninnovations.ecomerce.ui.manage.ManageCategoriesActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {

    private lateinit var categorySpinner: Spinner
    private lateinit var brandSpinner: Spinner
    private lateinit var categories: List<Categoria>
    private lateinit var brands: List<Marca>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)

        val editTextProductName = view.findViewById<EditText>(R.id.editTextProductName)
        val editTextProductPrice = view.findViewById<EditText>(R.id.editTextProductPrice)
        val editTextProductStock = view.findViewById<EditText>(R.id.editTextProductStock)
        val editTextNewCategoryName = view.findViewById<EditText>(R.id.editTextNewCategoryName)
        val editTextNewBrandName = view.findViewById<EditText>(R.id.editTextNewBrandName)
        val buttonAddProduct = view.findViewById<Button>(R.id.buttonAddProduct)
        val buttonManageCategories = view.findViewById<Button>(R.id.buttonManageCategories)
        val buttonManageBrands = view.findViewById<Button>(R.id.buttonManageBrands)

        categorySpinner = view.findViewById(R.id.categorySpinner)
        brandSpinner = view.findViewById(R.id.brandSpinner)

        loadCategoriesAndBrands()

        buttonManageCategories.setOnClickListener {
            // Navegar a la pantalla de gestión de categorías
            val intent = Intent(requireContext(), ManageCategoriesActivity::class.java)
            startActivity(intent)
        }

        buttonManageBrands.setOnClickListener {
            // Navegar a la pantalla de gestión de marcas
            val intent = Intent(requireContext(), ManageBrandsActivity::class.java)
            startActivity(intent)
        }

        buttonAddProduct.setOnClickListener {
            val productName = editTextProductName.text.toString()
            val productPrice = editTextProductPrice.text.toString().toDouble()
            val productStock = editTextProductStock.text.toString().toInt()
            val newCategoryName = editTextNewCategoryName.text.toString()
            val newBrandName = editTextNewBrandName.text.toString()

            val selectedCategory = categorySpinner.selectedItem as Categoria
            val selectedBrand = brandSpinner.selectedItem as Marca

            val categoryId = if (newCategoryName.isEmpty()) selectedCategory.categoryId else null
            val brandId = if (newBrandName.isEmpty()) selectedBrand.brandId else null

            val newCategory = if (newCategoryName.isNotEmpty()) Categoria(name = newCategoryName) else null
            val newBrand = if (newBrandName.isNotEmpty()) Marca(name = newBrandName) else null

            val newProductRequest = CreateProductoRequest(
                name = productName,
                price = productPrice,
                stock = productStock,
                categoryId = categoryId,
                brandId = brandId,
                newCategory = newCategory,
                newBrand = newBrand
            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitClient.instance.addProduct(newProductRequest)
                    Toast.makeText(context, "Producto creado exitosamente", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error al crear producto", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun loadCategoriesAndBrands() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                categories = RetrofitClient.instance.getCategories()
                brands = RetrofitClient.instance.getBrands()

                val categoryAdapter = CustomSpinnerAdapter(requireContext(), categories)
                categorySpinner.adapter = categoryAdapter

                val brandAdapter = CustomSpinnerAdapter(requireContext(), brands)
                brandSpinner.adapter = brandAdapter

            } catch (e: Exception) {
                Toast.makeText(context, "Error al cargar categorías y marcas", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.edisoninnovations.ecomerce.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.edisoninnovations.ecomerce.R
import com.edisoninnovations.ecomerce.model.Categoria
import com.edisoninnovations.ecomerce.model.Marca
import com.edisoninnovations.ecomerce.model.Producto

class CustomSpinnerE<T>(
    context: Context,
    private val items: List<T>
) : ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, items) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(parent, position, convertView, android.R.layout.simple_spinner_item)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(parent, position, convertView, android.R.layout.simple_spinner_dropdown_item)
    }

    private fun createViewFromResource(parent: ViewGroup, position: Int, convertView: View?, resource: Int): View {
        val view: TextView = convertView as? TextView
            ?: LayoutInflater.from(context).inflate(resource, parent, false) as TextView
        val item = items[position]
        view.text = when (item) {
            is Categoria -> item.name
            is Marca -> item.name
            is Producto -> item.name // Mostrar solo el nombre del producto
            else -> item.toString()
        }
        return view
    }

    fun getCustomPosition(item: T): Int {
        return items.indexOf(item)
    }
}

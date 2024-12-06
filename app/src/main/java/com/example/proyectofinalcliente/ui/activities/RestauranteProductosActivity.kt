package com.example.proyectofinalcliente.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.models.OrderDetail
import com.example.proyectofinalcliente.models.OrderRequest
import com.example.proyectofinalcliente.models.Restaurant
import com.example.proyectofinalcliente.ui.adapters.ProductAdapter
import com.example.proyectofinalcliente.repositories.RestaurantRepository
import com.example.proyectofinalcliente.ui.viewmodels.OrderViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class RestauranteProductosActivity : AppCompatActivity() {

    private lateinit var productAdapter: ProductAdapter
    private val restaurantRepository = RestaurantRepository
    private lateinit var orderViewModel: OrderViewModel
    private var currentRestaurant: Restaurant? = null // Variable para almacenar el restaurante actual

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurante_productos)

        // Configuración del RecyclerView
        productAdapter = ProductAdapter()
        findViewById<RecyclerView>(R.id.recyclerView2).apply {
            layoutManager = LinearLayoutManager(this@RestauranteProductosActivity)
            adapter = productAdapter
        }

        //Iniciar el viewmodel
        orderViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[OrderViewModel::class.java]

        // Obtener el ID del restaurante
        val restaurantId = intent.getIntExtra("restaurant_id", -1)
        if (restaurantId == -1) {
            Toast.makeText(this, "Error al cargar restaurante. ID no válido.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Cargar datos del restaurante
        val token = getSharedPreferences("UserPreferences", MODE_PRIVATE).getString("ACCESS_TOKEN", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token inválido. Por favor inicie sesión nuevamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val restaurant = restaurantRepository.getRestaurantDetails(restaurantId, token)
                currentRestaurant = restaurant // Almacenar el restaurante actual
                findViewById<TextView>(R.id.lblRestName).text = restaurant.name
                productAdapter.submitList(restaurant.products)
            } catch (e: Exception) {
                Log.e("RestauranteProductosActivity", "Error fetching restaurant details", e)
                Toast.makeText(this@RestauranteProductosActivity, "Error al cargar productos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón "Hacer Pedido"
        findViewById<Button>(R.id.btnHacerPedido).setOnClickListener {
            val orderRequest = createOrderRequest()
            if (orderRequest == null) {
                Toast.makeText(this, "No se han seleccionado productos o datos del restaurante no están disponibles.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            orderViewModel.createOrder(orderRequest)
        }

        // Observadores para el ViewModel
        orderViewModel.orderResponse.observe(this) { response ->
            Toast.makeText(this, "Pedido realizado: ID ${response.id}", Toast.LENGTH_SHORT).show()
        }

        orderViewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun createOrderRequest(): OrderRequest? {
        val selectedProducts = productAdapter.getSelectedProducts()
        if (selectedProducts.isEmpty() || currentRestaurant == null) return null

        val orderDetails = selectedProducts.map { product ->
            OrderDetail(
                product_id = product.id,
                qty = product.quantity,
                price = (product.price.toDouble() * product.quantity).toString()
            )
        }

        val total = orderDetails.sumOf { it.price.toDouble() }

        // Usar los valores del restaurante actual
        return OrderRequest(
            restaurant_id = currentRestaurant!!.id,
            total = total,
            address = currentRestaurant!!.address,
            latitude = currentRestaurant!!.latitude,
            longitude = currentRestaurant!!.longitude,
            details = orderDetails
        )
    }
}

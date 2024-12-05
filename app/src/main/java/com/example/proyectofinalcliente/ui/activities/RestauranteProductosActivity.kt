package com.example.proyectofinalcliente.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.ui.adapters.ProductAdapter
import com.example.proyectofinalcliente.repositories.RestaurantRepository
import kotlinx.coroutines.launch

class RestauranteProductosActivity : AppCompatActivity() {

    private lateinit var productAdapter: ProductAdapter
    private val restaurantRepository = RestaurantRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurante_productos)

        // Configuración del RecyclerView
        productAdapter = ProductAdapter()
        findViewById<RecyclerView>(R.id.recyclerView2).apply {
            layoutManager = LinearLayoutManager(this@RestauranteProductosActivity)
            adapter = productAdapter
        }

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
                findViewById<TextView>(R.id.lblRestName).text = restaurant.name
                productAdapter.submitList(restaurant.products)
            } catch (e: Exception) {
                Log.e("RestauranteProductosActivity", "Error fetching restaurant details", e)
                Toast.makeText(this@RestauranteProductosActivity, "Error al cargar productos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}




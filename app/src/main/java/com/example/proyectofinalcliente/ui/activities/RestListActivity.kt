package com.example.proyectofinalcliente.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.api.ApiService
import com.example.proyectofinalcliente.databinding.ActivityRestListBinding
import com.example.proyectofinalcliente.models.Restaurant
import com.example.proyectofinalcliente.repositories.RestaurantRepository
import com.example.proyectofinalcliente.repositories.RetrofitRepository
import com.example.proyectofinalcliente.ui.adapters.RestaurantAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestListBinding
    private val restaurantAdapter by lazy { RestaurantAdapter(mutableListOf()) { onRestaurantClick(it) } }
    private var allRestaurants = mutableListOf<Restaurant>() // Lista completa de restaurantes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchRestaurants()
        setupListeners()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@RestListActivity)
            adapter = restaurantAdapter
        }
    }

    private fun fetchRestaurants() {
        val token = getSharedPreferences("UserPreferences", MODE_PRIVATE).getString("ACCESS_TOKEN", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show()
            return
        }

        RestaurantRepository.getRestaurants(token) { restaurants, error ->
            if (restaurants != null) {
                allRestaurants.clear()
                allRestaurants.addAll(restaurants)
                restaurantAdapter.updateData(restaurants)
            } else {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.txtedtBuscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                filterRestaurants(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.btnVerPedidos.setOnClickListener {
            val intent = Intent(this, OrdersListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun filterRestaurants(query: String) {
        if (query.isEmpty()) {
            restaurantAdapter.updateData(allRestaurants) // Mostrar todos si no hay filtro
        } else {
            val filteredList = allRestaurants.filter { it.name.contains(query, ignoreCase = true) }
            restaurantAdapter.updateData(filteredList)
        }
    }

    private fun onRestaurantClick(restaurant: Restaurant) {
        Toast.makeText(this, "Seleccionaste: ${restaurant.name}", Toast.LENGTH_SHORT).show()
        // Implementa la lógica para abrir los detalles del restaurante aquí
    }
}


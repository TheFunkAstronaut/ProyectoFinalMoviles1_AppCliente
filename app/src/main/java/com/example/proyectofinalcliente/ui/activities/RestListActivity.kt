package com.example.proyectofinalcliente.ui.activities

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchRestaurants()
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
                restaurantAdapter.updateData(restaurants)
            } else {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun onRestaurantClick(restaurant: Restaurant) {
        Toast.makeText(this, "Seleccionaste: ${restaurant.name}", Toast.LENGTH_SHORT).show()
    }
}

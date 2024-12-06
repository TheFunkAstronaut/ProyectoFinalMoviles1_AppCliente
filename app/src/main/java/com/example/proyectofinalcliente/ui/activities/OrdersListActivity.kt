package com.example.proyectofinalcliente.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.models.OrderResponse
import com.example.proyectofinalcliente.repositories.OrderRepository
import com.example.proyectofinalcliente.ui.adapters.OrdersAdapter

class OrdersListActivity : AppCompatActivity() {

    private lateinit var ordersAdapter: OrdersAdapter
    private val orders = mutableListOf<OrderResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_list)

        setupRecyclerView()
        fetchOrders()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)
        ordersAdapter = OrdersAdapter(orders) { order ->
            // Acción cuando se hace clic en un item
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("ORDER_ID", order.id)
            intent.putExtra("LATITUDE", order.latitude)
            intent.putExtra("LONGITUDE", order.longitude)
            startActivity(intent)
        }
        recyclerView.adapter = ordersAdapter
    }

    private fun fetchOrders() {
        val token = getSharedPreferences("UserPreferences", MODE_PRIVATE).getString("ACCESS_TOKEN", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_SHORT).show()
            return
        }

        // Hacer la llamada a la API para obtener los pedidos
        OrderRepository.getOrders(token) { fetchedOrders, error ->
            if (fetchedOrders != null) {
                orders.clear()
                orders.addAll(fetchedOrders)
                ordersAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


package com.example.proyectofinalcliente.ui.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.databinding.ItemRestaurantBinding
import com.example.proyectofinalcliente.models.Restaurant
import com.example.proyectofinalcliente.ui.activities.RestauranteProductosActivity

class RestaurantAdapter(
    private val restaurants: MutableList<Restaurant>,
    private val onClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        with(holder.binding) {
            txtRestaurantName.text = restaurant.name
            txtRestaurantAddress.text = restaurant.address
            Glide.with(imgLogo.context)
                .load(restaurant.logo)
                .placeholder(R.drawable.ic_placeholder)
                .into(imgLogo)

            root.setOnClickListener {
                onClick(restaurant)
                Log.d("RestaurantAdapter", "Clicked restaurant ID: ${restaurant.id}")
            }
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, RestauranteProductosActivity::class.java)
            intent.putExtra("restaurant_id", restaurant.id)
            context.startActivity(intent)
            Log.d("RestaurantAdapter", "Starting RestauranteProductosActivity with ID: ${restaurant.id}")
        }
    }


    fun updateData(newRestaurants: List<Restaurant>) {
        restaurants.clear()
        restaurants.addAll(newRestaurants)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = restaurants.size
}


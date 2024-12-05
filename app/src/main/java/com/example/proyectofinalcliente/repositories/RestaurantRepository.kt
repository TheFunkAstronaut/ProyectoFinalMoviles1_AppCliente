package com.example.proyectofinalcliente.repositories

import com.example.proyectofinalcliente.api.ApiService
import com.example.proyectofinalcliente.models.Restaurant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RestaurantRepository {
    private val apiService: ApiService = RetrofitRepository.getRetrofitInstance().create(ApiService::class.java)

    fun getRestaurants(token: String, callback: (List<Restaurant>?, String?) -> Unit) {
        apiService.getRestaurants("Bearer $token").enqueue(object : Callback<List<Restaurant>> {
            override fun onResponse(call: Call<List<Restaurant>>, response: Response<List<Restaurant>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Restaurant>>, t: Throwable) {
                callback(null, "Error de red: ${t.message}")
            }
        })
    }

    suspend fun getRestaurantDetails(id: Int, token: String): Restaurant {
        return apiService.getRestaurantDetails("Bearer $token", id)
    }
}



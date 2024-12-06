package com.example.proyectofinalcliente.repositories

import com.example.proyectofinalcliente.api.ApiService
import com.example.proyectofinalcliente.models.OrderRequest
import com.example.proyectofinalcliente.models.OrderResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object OrderRepository {
    private val apiService: ApiService = RetrofitRepository.getRetrofitInstance().create(ApiService::class.java)

    suspend fun createOrder(token: String, orderRequest: OrderRequest): OrderResponse {
        return apiService.createOrder(token, orderRequest)
    }
    fun getOrders(token: String, callback: (List<OrderResponse>?, String?) -> Unit) {
        apiService.getOrders("Bearer $token").enqueue(object : Callback<List<OrderResponse>> {
            override fun onResponse(call: Call<List<OrderResponse>>, response: Response<List<OrderResponse>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error al obtener los pedidos")
                }
            }

            override fun onFailure(call: Call<List<OrderResponse>>, t: Throwable) {
                callback(null, t.message)
            }
        })
    }
}

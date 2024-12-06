package com.example.proyectofinalcliente.api

import com.example.proyectofinalcliente.models.LoginRequest
import com.example.proyectofinalcliente.models.LoginResponse
import com.example.proyectofinalcliente.models.OrderRequest
import com.example.proyectofinalcliente.models.OrderResponse
import com.example.proyectofinalcliente.models.RegisterRequest
import com.example.proyectofinalcliente.models.RegisterResponse
import com.example.proyectofinalcliente.models.Restaurant
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("users/login")
    fun login(@Body credentials: LoginRequest): Call<LoginResponse>

    @POST("users")
    fun register(@Body user: RegisterRequest): Call<RegisterResponse>

    @GET("restaurants")
    fun getRestaurants(@Header("Authorization") token: String): Call<List<Restaurant>>

    @GET("restaurants/{id}")
    suspend fun getRestaurantDetails(@Header("Authorization") token: String, @Path("id") id: Int): Restaurant

    @POST("orders")
    suspend fun createOrder(@Header("Authorization") token: String, @Body orderRequest: OrderRequest): OrderResponse

    @GET("orders")
    fun getOrders(@Header("Authorization") token: String): Call<List<OrderResponse>>
}



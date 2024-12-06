package com.example.proyectofinalcliente.ui.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinalcliente.models.OrderRequest
import com.example.proyectofinalcliente.models.OrderResponse
import com.example.proyectofinalcliente.repositories.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val _orderResponse = MutableLiveData<OrderResponse>()
    val orderResponse: LiveData<OrderResponse> get() = _orderResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val orderRepository = OrderRepository

    fun createOrder(orderRequest: OrderRequest) {
        val token = getApplication<Application>().getSharedPreferences("UserPreferences", MODE_PRIVATE)
            .getString("ACCESS_TOKEN", null)

        if (token.isNullOrEmpty()) {
            _errorMessage.postValue("Token no válido. Por favor, inicia sesión nuevamente.")
            return
        }

        viewModelScope.launch {
            try {
                val response = orderRepository.createOrder("Bearer $token", orderRequest)
                _orderResponse.postValue(response)
            } catch (e: Exception) {
                _errorMessage.postValue("Error al realizar el pedido: ${e.message}")
                Log.e("OrderViewModel", "Error al realizar el pedido", e)
            }
        }
    }
}


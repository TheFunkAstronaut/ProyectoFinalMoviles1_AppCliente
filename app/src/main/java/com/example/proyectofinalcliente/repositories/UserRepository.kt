package com.example.proyectofinalcliente.repositories

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.proyectofinalcliente.api.ApiService
import com.example.proyectofinalcliente.models.LoginRequest
import com.example.proyectofinalcliente.models.LoginResponse
import com.example.proyectofinalcliente.models.RegisterRequest
import com.example.proyectofinalcliente.models.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object UserRepository {
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    }

    fun login(credentials: LoginRequest, onSuccess: (LoginResponse) -> Unit, onError: (Throwable) -> Unit) {
        val apiService = RetrofitRepository.getRetrofitInstance().create(ApiService::class.java)
        apiService.login(credentials).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        saveAccessToken(it.access_token)
                        onSuccess(it)
                    }
                } else {
                    val errorMessage = "Error al iniciar sesión: ${response.code()} ${response.message()}"
                    Log.e("LoginError", errorMessage)
                    onError(Throwable(errorMessage))
                }
            }


            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginError", "Error de red: ${t.localizedMessage}", t)
                onError(t)
            }

        })
    }

    fun register(user: RegisterRequest, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        val apiService = RetrofitRepository.getRetrofitInstance().create(ApiService::class.java)
        apiService.register(user).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(Throwable("Error al registrar usuario"))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                onError(t)
            }
        })
    }

    private fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString("ACCESS_TOKEN", token).apply()
    }
}

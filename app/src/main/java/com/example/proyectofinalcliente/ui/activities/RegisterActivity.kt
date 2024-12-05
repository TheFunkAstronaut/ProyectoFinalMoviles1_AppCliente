package com.example.proyectofinalcliente.ui.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.models.RegisterRequest
import com.example.proyectofinalcliente.repositories.UserRepository

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val btnVolver = findViewById<Button>(R.id.button2)
        val btnRegistrar = findViewById<Button>(R.id.button)
        val nombre = findViewById<EditText>(R.id.txtedtNombre)
        val correo = findViewById<EditText>(R.id.txtEdt)
        val password = findViewById<EditText>(R.id.txtEdtCorreo)

        btnVolver.setOnClickListener {
            finish()
        }

        btnRegistrar.setOnClickListener {
            val user = RegisterRequest(
                name = nombre.text.toString(),
                email = correo.text.toString(),
                password = password.text.toString(),
                role = 1 // Rol constante para clientes
            )

            UserRepository.register(user, {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                finish()
            }, { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            })
        }
    }
}


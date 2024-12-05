package com.example.proyectofinalcliente.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.models.LoginRequest
import com.example.proyectofinalcliente.repositories.UserRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Inicializa UserRepository con el contexto
        UserRepository.init(this)  // Aquí inicializas sharedPreferences

        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)
        val btnIniciarSesion = findViewById<Button>(R.id.btnIniciarSesion)
        val correo = findViewById<EditText>(R.id.txtedtCorreo)
        val password = findViewById<EditText>(R.id.txtedtpassword)

        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnIniciarSesion.setOnClickListener {
            val credentials = LoginRequest(correo.text.toString(), password.text.toString())
            UserRepository.login(credentials, { response ->
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, RestListActivity::class.java)
                intent.putExtra("TOKEN", response.access_token)
                startActivity(intent)
            }, { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            })
        }
    }
}

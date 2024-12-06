package com.example.proyectofinalcliente.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.proyectofinalcliente.databinding.ActivityMapsBinding
import com.example.proyectofinalcliente.R

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener los datos del Intent
        val latitude = intent.getStringExtra("LATITUDE")?.toDoubleOrNull()
        val longitude = intent.getStringExtra("LONGITUDE")?.toDoubleOrNull()

        // Inicializa el SupportMapFragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)  // Esto llamará a onMapReady cuando el mapa esté listo

        // Verificar si se pasó la latitud y longitud
        if (latitude != null && longitude != null) {
            val location = LatLng(latitude, longitude)

            // Agregar el marcador solo después de que el mapa esté listo
            mapFragment.getMapAsync {
                it.addMarker(MarkerOptions().position(location).title("Ubicación del Pedido"))
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        }
    }

    // Este callback se llama cuando el mapa está listo para ser usado
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}


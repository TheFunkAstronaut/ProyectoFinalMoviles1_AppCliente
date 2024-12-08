package com.example.proyectofinalcliente.ui.activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.proyectofinalcliente.databinding.ActivityMapsBinding
import com.example.proyectofinalcliente.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var orderStatusTextView: TextView? = null

    // Marcadores de delivery y restaurante
    private var deliveryMarker: Marker? = null
    private var restaurantMarker: Marker? = null

    // Coordenadas iniciales del delivery y destino
    private val initialDeliveryLocation = LatLng(-17.761086198068963, -63.193332713425534)
    private var restaurantLocation: LatLng? = null

    // Variables de estado
    private var orderStatus = ""
    private var isDeliveryAtRestaurant = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializa el SupportMapFragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Obtener TextView para mostrar el estado del pedido
        orderStatusTextView = findViewById(R.id.txtOrderStatus)

        // Obtener estado del pedido y actualizar la UI
        orderStatus = intent.getStringExtra("ORDER_STATUS") ?: "Desconocido"
        updateOrderStatus(orderStatus)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Obtener los datos del Intent
        val latitude = intent.getStringExtra("LATITUDE")?.toDoubleOrNull()
        val longitude = intent.getStringExtra("LONGITUDE")?.toDoubleOrNull()

        if (latitude != null && longitude != null) {
            val location = LatLng(latitude, longitude)
            restaurantLocation = location
            restaurantMarker = mMap.addMarker(MarkerOptions().position(location).title("Ubicación del Restaurante"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }

        // Mostrar ubicación del cliente si los permisos están concedidos
        if (checkPermissions()) {
            getUserLocation()
        } else {
            requestPermissions()
        }

        // Si el estado es "Aceptado por chofer", agregar marcador del delivery
        if (orderStatus == "1") {
            startDeliveryMovement()
        }
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(userLatLng).title("Mi ubicación"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
            }
        }.addOnFailureListener {
            Toast.makeText(this, "No se pudo obtener la ubicación del cliente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateOrderStatus(status: String) {
        val readableStatus = when (status) {
            "0" -> "Solicitado"
            "1" -> "Aceptado por chofer"
            "2" -> "En camino"
            "3" -> "Finalizado"
            else -> "Desconocido"
        }
        orderStatusTextView?.text = "Estado del envío: $readableStatus"
    }

    private fun startDeliveryMovement() {
        // Colocamos el marcador de delivery en la posición inicial
        deliveryMarker = mMap.addMarker(MarkerOptions().position(initialDeliveryLocation).title("Empleado de Delivery"))

        // Movimiento del delivery hacia el restaurante
        moveDeliveryToRestaurant()
    }

    private fun moveDeliveryToRestaurant() {
        val handler = Handler(Looper.getMainLooper())
        val startLocation = initialDeliveryLocation
        val destinationLocation = restaurantLocation ?: return

        val totalDistance = calculateDistance(startLocation, destinationLocation)
        var progress = 0
        val updateInterval: Long = 10000 // Actualizar cada 10 segundos (Long)

        val updateRunnable = object : Runnable {
            override fun run() {
                if (progress < totalDistance) {
                    progress += (totalDistance / 30).toInt() // Convertir a Int para evitar Type mismatch

                    val newLat = startLocation.latitude + (destinationLocation.latitude - startLocation.latitude) * (progress / totalDistance)
                    val newLng = startLocation.longitude + (destinationLocation.longitude - startLocation.longitude) * (progress / totalDistance)
                    val newLocation = LatLng(newLat, newLng)

                    // Actualizar la ubicación del marcador
                    deliveryMarker?.position = newLocation
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(newLocation))

                    // Si el delivery ha llegado al restaurante
                    if (!isDeliveryAtRestaurant && progress >= totalDistance) {
                        isDeliveryAtRestaurant = true
                        mMap.clear() // Limpiar solo el marcador del restaurante
                        restaurantMarker = null // Eliminar marcador del restaurante
                    }
                    handler.postDelayed(this, updateInterval) // Repetir cada 10 segundos
                }
            }
        }

        handler.post(updateRunnable)
    }


    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results)
        return results[0].toDouble()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}




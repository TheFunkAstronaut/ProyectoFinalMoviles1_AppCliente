package com.example.proyectofinalcliente.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.models.OrderResponse

class OrdersAdapter(private val orders: List<OrderResponse>, private val onItemClick: (OrderResponse) -> Unit) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_orders, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        private val txtProductName: TextView = itemView.findViewById(R.id.txtProductName)
        private val txtOrderId: TextView = itemView.findViewById(R.id.txtOrderId)
        private val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
        private val txtStatus: TextView = itemView.findViewById(R.id.txtStatus)

        fun bind(order: OrderResponse) {
            val firstOrderDetail = order.order_details.firstOrNull()

            // Asignamos los valores a los TextViews
            txtProductName.text = firstOrderDetail?.product?.name ?: "Producto no disponible"
            txtOrderId.text = "Pedido ID: #${order.id}"
            txtTotal.text = "Total: $${order.total}"

            // Determinamos el estado del envío
            txtStatus.text = when (order.status) {
                "1" -> "Enviado"
                "2" -> "Entregado"
                else -> "Pendiente"
            }

            // Cargamos la imagen del producto usando Glide o cualquier otra librería de imágenes
            Glide.with(itemView.context)
                .load(firstOrderDetail?.product?.image)
                .into(imgProduct)

            // Agregamos el clic en el item
            itemView.setOnClickListener {
                onItemClick(order)  // Llamamos a la función que maneja el clic
            }
        }
    }
}


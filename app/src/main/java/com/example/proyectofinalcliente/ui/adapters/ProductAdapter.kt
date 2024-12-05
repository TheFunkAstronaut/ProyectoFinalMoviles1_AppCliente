package com.example.proyectofinalcliente.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyectofinalcliente.R
import com.example.proyectofinalcliente.models.Product

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_productos, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.lblProductName)
        private val descriptionTextView: TextView = view.findViewById(R.id.lblProductDescription)
        private val priceTextView: TextView = view.findViewById(R.id.lblPrice)
        private val imageView: ImageView = view.findViewById(R.id.imageView)
        private val minusButton: Button = view.findViewById(R.id.btnMinus)
        private val plusButton: Button = view.findViewById(R.id.btnPlus)
        private val countTextView: TextView = view.findViewById(R.id.lblCount)

        private var count = 0

        fun bind(product: Product) {
            nameTextView.text = product.name
            descriptionTextView.text = product.description
            priceTextView.text = "Bs. ${product.price}"
            Glide.with(imageView.context).load(product.image).into(imageView)

            minusButton.setOnClickListener {
                if (count > 0) count--
                countTextView.text = count.toString()
            }

            plusButton.setOnClickListener {
                count++
                countTextView.text = count.toString()
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }
}


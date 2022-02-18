package com.example.primerappmvvmretrofitkotlin.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demo.core.BaseViewHolder
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.databinding.ProductsListBinding


class ProductsAdapterV2(
    private val context: Context,
    private val itemClickListener: OnProductClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var productsList = listOf<Product>()


    interface OnProductClickListener {
        //Método para gestionar el OnClick de los items del recyclerview
        fun onProductClick(product: Product)
        //Método para gestionar el click largo cuando se toca el item por unos segundos..
        fun onProductLongClick(product: Product, position: Int)
    }

    fun setProductList(productsList: List<Product>) {
        this.productsList = productsList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ProductsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = MainViewHolder (itemBinding, parent.context)

        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener
            itemClickListener.onProductClick(productsList[position])
        }

        holder.itemView.setOnLongClickListener {
            val position = holder.adapterPosition.takeIf { it != NO_POSITION }
                ?: return@setOnLongClickListener true

            itemClickListener.onProductLongClick(productsList[position], position)

            return@setOnLongClickListener true
        }

        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is MainViewHolder  -> holder.bind(productsList[position])
        }
    }

    override fun getItemCount(): Int = productsList.size

    private inner class MainViewHolder (val binding: ProductsListBinding, val context: Context) : BaseViewHolder<Product>(binding.root) {
        override fun bind(item: Product) {
            binding.txtName.text="${item.name}"
            binding.txtDescription.text="${item.description}"
            binding.txtBarcode.text="${item.barcode}"
            binding.txtQty.text = "Cantidad: ${item.qty}"
            binding.txtPrice.text = "S/${item.price}"
            Glide.with(context).load("${item.image_path}").centerCrop().into(binding.productImage)

        }
    }
}
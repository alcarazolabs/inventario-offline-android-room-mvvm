package com.example.primerappmvvmretrofitkotlin.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demo.core.BaseViewHolder
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.databinding.ProductsListBinding


class ProductsAdapter(
        private val productsList: List<Product>,
        private val itemClickListener: OnProductClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ProductsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = UsersViewHolder(itemBinding, parent.context)

        itemBinding.root.setOnClickListener {
            val position = holder.adapterPosition.takeIf { it != DiffUtil.DiffResult.NO_POSITION }
                    ?: return@setOnClickListener
            itemClickListener.onProductClick(productsList[position])
        }

        return holder
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is UsersViewHolder -> holder.bind(productsList[position])
        }
    }

    override fun getItemCount(): Int = productsList.size

    private inner class UsersViewHolder(val binding: ProductsListBinding, val context: Context) : BaseViewHolder<Product>(binding.root) {
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
package com.example.primerappmvvmretrofitkotlin.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView
import com.example.demo.core.BaseViewHolder
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.databinding.ProductsListBinding


class ProductsAdapterV2(
    private val itemClickListener: OnProductClickListener
) : RecyclerView.Adapter<BaseViewHolder<*>>(), Filterable {

    private var productsList = listOf<Product>()
    private var productsListBackup = listOf<Product>()

    interface OnProductClickListener {
        //Método para gestionar el OnClick de los items del recyclerview
        fun onProductClick(product: Product)
        //Método para gestionar el click largo cuando se toca el item por unos segundos..
        fun onProductLongClick(product: Product, position: Int)
    }

    fun setProductList(productsList: List<Product>) {
        this.productsList = productsList
        this.productsListBackup = productsList

        notifyDataSetChanged()
    }
    fun getProductList():List<Product>{
        return this.productsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val itemBinding = ProductsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = MainViewHolder (itemBinding)

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

    private inner class MainViewHolder (val binding: ProductsListBinding) : BaseViewHolder<Product>(binding.root) {
        override fun bind(item: Product) {
            val description = if (item.description.isNotEmpty()) item.description else "Sin descripción."
            val barcode = if (item.barcode.isNotEmpty()) item.barcode  else "Sin código."

            binding.txtName.text=item.name
            binding.txtDescription.text=description
            binding.txtBarcode.text=barcode
            binding.txtQty.text = "Cantidad: ${item.qty}"
            binding.txtPrice.text = "S/${item.price}"
            //Si el image_bitmap no es nulo:
            item.image_bitmap?.let { binding.productImage.setImageBitmap(item.image_bitmap!!) }

            //En caso de que el image_bitmap sea nulo se establece un drawable al imageView:
            //if (binding.productImage == null) binding.productImage.setBackgroundResource(R.drawable.ic_galery)
            //ya no se se setea un drawable por que por defecto tomara el drawable del imageview por defecto.


            //Glide.with(binding.productImage.context).load("${item.image_path}").centerCrop().into(binding.productImage)

        }
    }

    //Método para filtrar registro por la búsqueda del searchView. Se extiende de Filterable para poder usar este método.

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString().orEmpty().lowercase()
                return Filter.FilterResults().apply {
                    values = when {
                        queryString.isEmpty() -> productsListBackup
                        else -> productsListBackup.filter {
                            it.name.lowercase().contains(queryString) ||
                                    it.barcode.lowercase().contains(queryString) ||
                                    it.description.lowercase().contains(queryString)
                        }
                    }
                }

            }

            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                productsList = filterResults.values as List<Product>
                notifyDataSetChanged()
            }
        }

    }


}
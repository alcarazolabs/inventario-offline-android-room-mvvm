package com.example.inventariooffline.repository

import com.example.inventariooffline.data.local.LocalProductDatasource
import com.example.inventariooffline.data.model.Product
import kotlinx.coroutines.delay

class ProductRepositoryImpl (
    private val dataSourceLocal: LocalProductDatasource)
    : ProductRepository{

    override suspend fun saveProduct(product: Product) {
        dataSourceLocal.saveProduct(product)
    }

    override suspend fun getAllProducts(): List<Product> {
        //Agregar delay para ver efecto del shimmer.
        delay(3000)
        return dataSourceLocal.getAllProducts()
    }

    override suspend fun updateProduct(product: Product) {
        dataSourceLocal.updateProduct(product)
    }

    override suspend fun deleteProduct(product: Product) {
        dataSourceLocal.deleteProduct(product)
    }

    override suspend fun deleteAllProducts() {
        dataSourceLocal.deleteAllProducts()
    }


}
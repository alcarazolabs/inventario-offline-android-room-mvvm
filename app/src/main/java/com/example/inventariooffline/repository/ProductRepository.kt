package com.example.inventariooffline.repository

import com.example.inventariooffline.data.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun saveProduct(product: Product)
    suspend fun getAllProducts(): List<Product>
    fun allProducts(): Flow<List<Product>>
    suspend fun updateProduct(product: Product)
    suspend fun deleteProduct(product: Product)
    suspend fun deleteAllProducts()
    fun searchProduct(search:String?): Flow<List<Product>>
}
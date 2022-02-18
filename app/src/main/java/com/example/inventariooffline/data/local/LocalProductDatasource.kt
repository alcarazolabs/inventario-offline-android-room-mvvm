package com.example.inventariooffline.data.local

import com.example.inventariooffline.data.model.Product

class LocalProductDatasource (private val productDao: ProductDao){

    suspend fun saveProduct(product: Product){
        productDao.saveProduct(product)
    }

    suspend fun getAllProducts():List<Product>{
        return productDao.getAllProducts()
    }

    suspend fun updateProduct(product: Product){
        productDao.updateProduct(product)
    }
    suspend fun deleteProduct(product: Product){
        productDao.deleteProduct(product)
    }
    suspend fun deleteAllProducts(){
        productDao.deleteAllProducts()
    }


}

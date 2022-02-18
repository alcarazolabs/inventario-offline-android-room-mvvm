package com.example.inventariooffline.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.inventariooffline.data.model.Product
//Los dao tienen todos los métodos para acceder a la base de datos.
@Dao
interface ProductDao {
    //ctrl+Q y mause over la palabra amarilla para obtener documentacion

    @Insert(onConflict = OnConflictStrategy.REPLACE) //esta estrategia permite ignorar un registro si es igual a otro cuando se registre
    suspend fun saveProduct(product: Product)


    @Query("SELECT * FROM product_table_entity ORDER BY id DESC")
    suspend fun getAllProducts(): List<Product>

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM product_table_entity")
    suspend fun deleteAllProducts()

}
package com.example.inventariooffline.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.inventariooffline.data.model.Product
import kotlinx.coroutines.flow.Flow

//Los dao tienen todos los métodos para acceder a la base de datos.
@Dao
interface ProductDao {
    //ctrl+Q y mause over la palabra amarilla para obtener documentacion

    @Insert(onConflict = OnConflictStrategy.REPLACE) //esta estrategia permite ignorar un registro si es igual a otro cuando se registre
    suspend fun saveProduct(product: Product)

    //Forma normal si Flow
    @Query("SELECT * FROM product_table_entity ORDER BY id DESC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM product_table_entity ORDER BY id DESC")
     fun allProducts(): Flow<List<Product>>

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM product_table_entity")
    suspend fun deleteAllProducts()

    //buscar solo por nombre:
    // @Query("SELECT * FROM product_table_entity WHERE name LIKE '%' || :search || '%' ")
    //buscar en varios campos con like
    @Query("SELECT * FROM product_table_entity WHERE name LIKE '%' || :search || '%' OR barcode LIKE '%' || :search || '%' ")
    fun searchProduct(search: String?): Flow<List<Product>>

}
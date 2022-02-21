package com.example.inventariooffline.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.inventariooffline.core.Converters
import com.example.inventariooffline.data.model.Product

@Database(entities = [Product::class], version = 1)
@TypeConverters(Converters::class) //Se pasa el helper de converters a la base de datos para que la bd acepte almacenar objetos custom que no se aceptados comunmente por la bd. Se usa para almacenar el bitmap de la imagen del producto. En caso de no almacenar bitmaps remover esto.
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {

        private var INSTANCE: AppDatabase? = null
        //Singleton
        fun getDatabase(context: Context): AppDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "inventario_database"
            ).build()
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}
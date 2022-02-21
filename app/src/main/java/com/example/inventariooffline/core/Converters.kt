package com.example.inventariooffline.core

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {
    private val BITMAP_QUALITY = 20; //100 is normal.
    /*
    * Calidades:
    * - 100: Origina un bitmap de más de 1mb con formato de compresion PNG
    * - 20: Origina un bitmap de  aprox 48kb de acuerdo a los datos del app testeado
    * El cache del app se incrementa en ambas calidades PNG o JPG, se requiere borrar el cache luego de un tiempo.
    * Si se borra el cache este no origina que los bitmap se borre de la bd por que la data de ROOM reside en la data del app no en el cache.
    * */
    /*
    // Converters sin nullables
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
    */

    //Converter para obtener bitmap a partir del biteArray
    @TypeConverter
    fun toBitmap(bitmapdata: ByteArray?): Bitmap? {
        return bitmapdata?.let {
            BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.size)
        }
    }
    //Converter para convertir bitmap a byteArray y sea guardado en la base de datos.
    @TypeConverter
    fun fromBitmap(bmp: Bitmap?): ByteArray? {
        val stream = ByteArrayOutputStream()
        // bmp?.compress(Bitmap.CompressFormat.PNG, 100, stream) //Origina un bitmap de mas de 1 megabyte.
        bmp?.compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, stream)
        val byteArray: ByteArray = stream.toByteArray()
        return byteArray
    }
}
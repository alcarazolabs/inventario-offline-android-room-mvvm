package com.example.inventariooffline.presentation

import android.util.Log
import androidx.lifecycle.*
import com.example.inventariooffline.core.Resource
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProductViewModel (private val repo: ProductRepository) : ViewModel() {

    //Flow normal
    //val products = repo.allProducts().asLiveData()

    //Flow para buscar. Se llama al metodo del repo que hace la búsqueda con LIKE.
    // Un solo método que lista si no tiene nada como parámetro de busqueda o busca si le pasan parámetro gracias al Like
    val searcQueryProduct = MutableStateFlow("") //se inicia con un valor por defecto
    private val productsFlow = searcQueryProduct.flatMapLatest {
        repo.searchProduct(it)
    }
    val products = productsFlow.asLiveData()

    //######################### LiveData para obtener todos los products ###############
    fun getAllProducts() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.getAllProducts()))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
    // Se usó al inicio. Como alternativa se usó flow
    //##################################################################################


    //################################### saveProduct  ###########################

    fun saveProduct(
        product: Product
    ) = viewModelScope.launch {
        repo.saveProduct(product)
    }
    //###############################################################################################

    //################################### updateProduct  ###########################

    fun updateProduct(
        product: Product
    ) = viewModelScope.launch {
        repo.updateProduct(product)
    }
    //###############################################################################################

    //################################### deleteProduct  ###########################

    fun deleteProduct(
        product: Product
    ) = viewModelScope.launch {
        repo.deleteProduct(product)
    }
    //###############################################################################################

    //################################### deleteAllProducts  ###########################

    fun deleteAllProducts() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            repo.deleteAllProducts()
        }

    }
    //###############################################################################################

    //################################## Buscar producto con flow ###################################
    // Ejemplo usando un método custom del repositorio para hacer la búsqueda.
   /*
    val searcQueryProduct = MutableStateFlow("") //se inicia con un valor por defecto
    private val productsFlow = searcQueryProduct.flatMapLatest {
        repo.searchProduct(it)
    }
    val productsFromSearch = productsFlow.asLiveData()
    */
    //Fuente: https://youtu.be/bp7F-XB01kw?t=1538
    // Siguiente video https://www.youtube.com/watch?v=dd_Lv7AxqkY Combina multiple Flow
    //##############################################################################################
}

class ProductViewModelFactory(private val repo: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ProductRepository::class.java).newInstance(repo)
    }
}
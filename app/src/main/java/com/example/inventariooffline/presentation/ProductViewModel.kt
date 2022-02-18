package com.example.inventariooffline.presentation

import androidx.lifecycle.*
import com.example.inventariooffline.core.Resource
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel (private val repo: ProductRepository) : ViewModel() {

    //######################### LiveData para obtener todos los products ###############
    fun getAllProducts() = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(repo.getAllProducts()))
        } catch (e: Exception) {
            emit(Resource.Failure(e))
        }
    }
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
        repo.deleteAllProducts()
    }
    //###############################################################################################

}

class ProductViewModelFactory(private val repo: ProductRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ProductRepository::class.java).newInstance(repo)
    }
}
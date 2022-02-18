package com.example.inventariooffline.ui.product

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.room.ColumnInfo
import com.example.inventariooffline.R
import com.example.inventariooffline.data.local.AppDatabase
import com.example.inventariooffline.data.local.LocalProductDatasource
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.databinding.FragmentProductsBinding
import com.example.inventariooffline.databinding.FragmentRegisterProductBinding
import com.example.inventariooffline.presentation.ProductViewModel
import com.example.inventariooffline.presentation.ProductViewModelFactory
import com.example.inventariooffline.repository.ProductRepositoryImpl


class RegisterProductFragment : Fragment(R.layout.fragment_register_product) {

    private lateinit var binding : FragmentRegisterProductBinding
    //Instanciar viewModel
    private val viewModel by viewModels<ProductViewModel> { ProductViewModelFactory(ProductRepositoryImpl(
        LocalProductDatasource(AppDatabase.getDatabase(requireContext()).productDao()))
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //habilitar menu
        setHasOptionsMenu(true)
        binding = FragmentRegisterProductBinding.bind(view)


        binding.btnBarCode.setOnClickListener{
            Toast.makeText(requireContext(), "Abrir scanner..", Toast.LENGTH_LONG).show()
        }


    }

    fun validateInputs(  name: String,
                         barcode: String,
                         description: String,
                         price: String,
                         qty: String,
                         image_path: String): Boolean{
        return !(TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(barcode) ||
                TextUtils.isEmpty(description) ||
                TextUtils.isEmpty(price) ||
                TextUtils.isEmpty(qty) ||
                TextUtils.isEmpty(image_path))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_register_product, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //get item id to handle item clicks
        val id = item!!.itemId
        //handle item clicks
        if (id == R.id.menu_register){
            val name = binding.txtName.text.toString()
            val barcode = binding.txtBarcode.text.toString()
            val description = binding.txtDescription.text.toString()
            val price = binding.txtPrice.text.toString()
            val qty = binding.txtQty.text.toString()
            val image_path = "https://www.marketingdirecto.com/wp-content/uploads/2016/05/china-1-300x174.jpg"

            if(validateInputs(name,barcode,description,price,qty,image_path)){
                val product = Product(0,name,barcode,description,price.toDouble(),Integer.parseInt(qty),image_path)
                viewModel.saveProduct(product)

                Toast.makeText(requireContext(), "Registrado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerProductFragment_to_productsFragment)

            }else{
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }


        }
        return super.onOptionsItemSelected(item)
    }
}
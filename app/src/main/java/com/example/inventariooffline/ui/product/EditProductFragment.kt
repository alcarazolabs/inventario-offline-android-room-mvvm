package com.example.inventariooffline.ui.product

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventariooffline.R
import com.example.inventariooffline.data.local.AppDatabase
import com.example.inventariooffline.data.local.LocalProductDatasource
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.databinding.FragmentEditProductBinding
import com.example.inventariooffline.presentation.ProductViewModel
import com.example.inventariooffline.presentation.ProductViewModelFactory
import com.example.inventariooffline.repository.ProductRepositoryImpl
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class EditProductFragment : Fragment(R.layout.fragment_edit_product) {

    private lateinit var binding : FragmentEditProductBinding
    private val args by navArgs<EditProductFragmentArgs>()

    //Instanciar viewModel
    private val viewModel by viewModels<ProductViewModel> { ProductViewModelFactory(
        ProductRepositoryImpl(
            LocalProductDatasource(AppDatabase.getDatabase(requireContext()).productDao())
        )
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProductBinding.bind(view)
        //habilitar menu
        setHasOptionsMenu(true)

        binding.txtName.setText(args.productObject.name)
        binding.txtDescription.setText(args.productObject.description)
        binding.txtBarcode.setText(args.productObject.barcode)
        binding.txtQty.setText(args.productObject.qty.toString())
        binding.txtPrice.setText(args.productObject.price.toString())

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
        inflater.inflate(R.menu.menu_edit_product, menu)
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
                val product = Product(args.productObject.id,
                                    name,
                                    barcode,
                                    description,price.toDouble(),
                                    Integer.parseInt(qty),
                                    image_path)
                //Actualizar product
                viewModel.updateProduct(product)

                Toast.makeText(requireContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editProductFragment_to_productsFragment)

            }else{
                Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            }
        }

        if(id == R.id.menu_eliminar){

            deleteProduct()

        }


        return super.onOptionsItemSelected(item)
    }

    private fun deleteProduct(){
        //AlertDialog: https://www.youtube.com/watch?v=ptBW9tP2cHA
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar Producto - Confirmación")
            .setMessage("Esta seguro de eliminar el producto ${args.productObject.name}?")
            .setNegativeButton("No"){dialog, which ->
                //nothing to do..
            }
            .setPositiveButton("Si"){dialog, which ->
                viewModel.deleteProduct(args.productObject)
                Toast.makeText(requireContext(), "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editProductFragment_to_productsFragment)

            }
            .show()

    }
}
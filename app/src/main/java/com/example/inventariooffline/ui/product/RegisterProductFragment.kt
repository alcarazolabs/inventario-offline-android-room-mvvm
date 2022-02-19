package com.example.inventariooffline.ui.product

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.canhub.cropper.*
import com.example.inventariooffline.R
import com.example.inventariooffline.data.local.AppDatabase
import com.example.inventariooffline.data.local.LocalProductDatasource
import com.example.inventariooffline.data.model.Product
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


    //ActivityResult del ImageCropper con CropImageContract
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            binding.productImage.setImageURI(uriContent)

        } else {
            // an error occurred
            val exception = result.error
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //habilitar menu
        setHasOptionsMenu(true)
        binding = FragmentRegisterProductBinding.bind(view)

        // ResultLauncher para recibir el bundle que se enviará desde la actividad ScanBarcodeActivity.
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                //Obtener key del bundle
                val barcode = data?.getStringExtra("barcode")
                binding.txtBarcode.setText(barcode)
            }
        }


        binding.btnBarCode.setOnClickListener{
            Toast.makeText(requireContext(), "Abriendo scanner..", Toast.LENGTH_LONG).show()
            //Abrir actividad que muestra el scanner de codigo de barras
            val intent = Intent(activity, ScanBarcodeActivity::class.java)
            resultLauncher.launch(intent)
        }

        binding.btnTakeImage.setOnClickListener {
            //Abrir image cropper
            cropImage.launch(
                options {
                    setGuidelines(CropImageView.Guidelines.ON)
                }
            )

        }

    }

    fun validateInputs(  name: String,
                         qty: String,
                         price: String): Boolean{
        return !(TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(qty) ||
                TextUtils.isEmpty(price))
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

            if(validateInputs(name,qty,price)){
                val product = Product(0,name,barcode,description,price.toDouble(),Integer.parseInt(qty),image_path)
                viewModel.saveProduct(product)

                Toast.makeText(requireContext(), "Registrado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerProductFragment_to_productsFragment)

            }else{
                Toast.makeText(requireContext(), "El nombre, precio y cantidad son necesarios", Toast.LENGTH_SHORT).show()
            }


        }
        return super.onOptionsItemSelected(item)
    }


}
package com.example.inventariooffline.ui.product

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
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
    private var productImageUri : Uri? = null

    //Instanciar viewModel
    private val viewModel by viewModels<ProductViewModel> { ProductViewModelFactory(
        ProductRepositoryImpl(
            LocalProductDatasource(AppDatabase.getDatabase(requireContext()).productDao())
        )
    ) }

    //ActivityResult del ImageCropper con CropImageContract
    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Obtener Uri
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(requireContext()) // optional usage
            //Establecer imagen al imageView a partir de la Uri.
            binding.productImage.setImageURI(uriContent)
            //Guardar uri en la variable productImageUri
            productImageUri = uriContent

        } else {
            // an error occurred
            val exception = result.error
        }
    }

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
        args.productObject.image_bitmap?.let { binding.productImage.setImageBitmap(args.productObject.image_bitmap) }

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

        binding.productImage.setOnClickListener {
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

            if(validateInputs(name,qty,price)){
                //Si se selecciono una nueva imagen.
                productImageUri?.let{
                    val product = Product(args.productObject.id,name,barcode,description,price.toDouble(),Integer.parseInt(qty),getBitmap(productImageUri!!))
                    viewModel.updateProduct(product)
                }
                //Si no se selecciono imágen
                if(productImageUri==null){
                    //Obtener el bitmap del imageview
                    //val bitmap = binding.productImage.getDrawable().toBitmap()

                    val product = Product(args.productObject.id,name,barcode,description,price.toDouble(),Integer.parseInt(qty),args.productObject.image_bitmap)
                    viewModel.updateProduct(product)
                }

                Toast.makeText(requireContext(), "Actualizado correctamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editProductFragment_to_productsFragment)

            }else{
                Toast.makeText(requireContext(), "El nombre, precio y cantidad son necesarios.", Toast.LENGTH_SHORT).show()
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

    private  fun getBitmap(imageUri: Uri): Bitmap {
        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.parse(imageUri.toString()))

        return bitmap
    }

}
package com.example.inventariooffline.ui.product

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventariooffline.R
import com.example.inventariooffline.core.Resource
import com.example.inventariooffline.data.local.AppDatabase
import com.example.inventariooffline.data.local.LocalProductDatasource
import com.example.inventariooffline.data.model.Product
import com.example.inventariooffline.databinding.FragmentProductsBinding
import com.example.inventariooffline.presentation.ProductViewModel
import com.example.inventariooffline.presentation.ProductViewModelFactory
import com.example.inventariooffline.repository.ProductRepositoryImpl
import com.example.primerappmvvmretrofitkotlin.ui.main.adapters.ProductsAdapterV2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class ProductsFragment : Fragment(R.layout.fragment_products), ProductsAdapterV2.OnProductClickListener {
    private lateinit var binding : FragmentProductsBinding
    //Instanciar viewModel
    private val viewModel by viewModels<ProductViewModel> { ProductViewModelFactory(
        ProductRepositoryImpl(
        LocalProductDatasource(AppDatabase.getDatabase(requireContext()).productDao())
        )
    ) }

    private lateinit var productsRecyclerViewAdapterV2: ProductsAdapterV2

    //resultLaunche del intent para guardar documento en almacenamiento externo
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            var uri = result?.data?.data
            uri?.let {
                val outputStream = requireContext().contentResolver.openOutputStream(uri!!)
                //Get the list of products
                val productList = productsRecyclerViewAdapterV2.getProductList()
                //convert the list of items to csvFormat
                val csvData = toCsvRow(productList)
                //write the data
                outputStream?.write(csvData.toByteArray())
                outputStream?.close()

                Toast.makeText(requireContext(), "Exportado Correctamente", Toast.LENGTH_SHORT).show()
            }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instanciar adapater del recyclerview
        productsRecyclerViewAdapterV2 = ProductsAdapterV2(this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductsBinding.bind(view)
        setObservers()
        //habilitar menu
        setHasOptionsMenu(true)
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = productsRecyclerViewAdapterV2

        binding.btnAddProduct.setOnClickListener{
            findNavController().navigate(R.id.action_productsFragment_to_registerProductFragment)
        }



        //Ocultar shimmer o efecto de pre-carga, lo normal es con pocos datos que no se vea.
        hideViewLoading()
    }
    private fun hideViewLoading() {
        binding.shimmerFrameLayout.stopShimmerAnimation()
        binding.shimmerFrameLayout.visibility = View.GONE
    }
    private fun showShimmerLoading() {
        binding.shimmerFrameLayout.startShimmerAnimation()
        binding.shimmerFrameLayout.visibility = View.GONE
    }

    private fun setObservers() {
        viewModel.products.observe(viewLifecycleOwner) {
            productsRecyclerViewAdapterV2.setProductList(it)
        }
    }


    fun setupObserversv1(){
        //De esta manera si se puede ver el efecto del shimmer.

        viewModel.getAllProducts().observe(viewLifecycleOwner,{
            when(it){
                is Resource.Loading ->{
                    binding.shimmerFrameLayout.startShimmerAnimation()
                    binding.shimmerFrameLayout.visibility = View.VISIBLE
                }
                is Resource.Success ->{

                    binding.shimmerFrameLayout.stopShimmerAnimation()
                    binding.shimmerFrameLayout.visibility = View.GONE
                    //Setear data al recyclerview
                   // binding.rvProducts.adapter = ProductsAdapter(it.data, this@ProductsFragment) //Forma normal ProductsAdapater
                    productsRecyclerViewAdapterV2.setProductList(it.data)
                }
                is Resource.Failure ->{
                    Toast.makeText(requireContext(), "No se pudo obtener los productos", Toast.LENGTH_SHORT).show()

                }
            }
        })

    }

    override fun onProductClick(product: Product) {
        Toast.makeText(requireContext(), "Producto ${product.name}", Toast.LENGTH_SHORT).show()

        val action = ProductsFragmentDirections.actionProductsFragmentToEditProductFragment(product)
        findNavController().navigate(action)
    }

    override fun onProductLongClick(product: Product, position: Int) {
        Toast.makeText(requireContext(), "Agregar a favoritos.. En construcción", Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
        super.onResume()
        binding.shimmerFrameLayout.startShimmerAnimation()
    }

    override fun onPause() {
        binding.shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_products, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //get item id to handle item clicks
        val id = item!!.itemId
        //handle item clicks

        if(id == R.id.menu_eliminar){
            deleteAllProduct()
        }

        if(id == R.id.menu_exportar){
            //Crear intent para abrir almacenamiento externo
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
              addCategory(Intent.CATEGORY_OPENABLE)
                //type = "text/plain" use this only for .txt
                type = "text/csv"
                putExtra(Intent.EXTRA_TITLE, "Inventario-database-${getDate()}.csv")
                putExtra(DocumentsContract.EXTRA_INITIAL_URI,"")
            }
            //lanzar intent.
            resultLauncher.launch(intent)

        }


        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllProduct(){
        //AlertDialog: https://www.youtube.com/watch?v=ptBW9tP2cHA
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Eliminar Productos - Confirmación")
            .setMessage("Esta seguro de eliminar todos los productos de la base de datos?")
            .setNegativeButton("No"){dialog, which ->
                //nothing to do..
            }
            .setPositiveButton("Si"){dialog, which ->
                viewModel.deleteAllProducts()

                Toast.makeText(requireContext(), "Productos eliminados correctamente", Toast.LENGTH_SHORT).show()

            }
            .show()

    }

    private fun toCsvRow(list:List<Product>) : String {
        var data : String? = ""
        data += "id,nombre,precio,cantidad,descripción,codigo_barras\n"
        list.forEach {
                data += "${it.id},${it.name},${it.price},${it.qty},${it.description},${it.barcode}\n"
            }
        return data!!
    }

    private fun getDate():String{
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)+1
        val day = c.get(Calendar.DAY_OF_MONTH)

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return "${year}-${month}-${day}_${hour}:${minute}"
    }
}
package com.example.inventariooffline.ui.product

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
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
import com.example.primerappmvvmretrofitkotlin.ui.main.adapters.ProductsAdapter
import com.example.primerappmvvmretrofitkotlin.ui.main.adapters.ProductsAdapterV2
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ProductsFragment : Fragment(R.layout.fragment_products), ProductsAdapterV2.OnProductClickListener {
    private lateinit var binding : FragmentProductsBinding
    //Instanciar viewModel
    private val viewModel by viewModels<ProductViewModel> { ProductViewModelFactory(
        ProductRepositoryImpl(
        LocalProductDatasource(AppDatabase.getDatabase(requireContext()).productDao())
        )
    ) }

    private lateinit var productsRecyclerViewAdapterV2: ProductsAdapterV2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //instanciar adapater del recyclerview
        productsRecyclerViewAdapterV2 = ProductsAdapterV2(requireContext(), this)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductsBinding.bind(view)
        //habilitar menu
        setHasOptionsMenu(true)
        binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProducts.adapter = productsRecyclerViewAdapterV2

        binding.btnAddProduct.setOnClickListener{
            findNavController().navigate(R.id.action_productsFragment_to_registerProductFragment)
        }

        fetchAllProducts()
    }

    fun fetchAllProducts(){

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
                //Se llama a setup observers para refrescar la llamada a la base de datos. Si no se hace el recyclerview no se actualiza.
                fetchAllProducts()

                Toast.makeText(requireContext(), "Productos eliminados correctamente", Toast.LENGTH_SHORT).show()

            }
            .show()

    }

}
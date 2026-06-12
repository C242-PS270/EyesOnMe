package com.capstone.eyesonme.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.eyesonme.views.GridSpacingItemDecoration
import com.capstone.eyesonme.views.ImageAdapter
import com.capstone.eyesonme.views.ImageCategory
import com.capstone.eyesonme.data.ImageDataSource
import com.capstone.eyesonme.data.ImageDatabase
import com.capstone.eyesonme.views.ImageItem
import com.capstone.eyesonme.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imageDataSource: ImageDataSource
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    // Gunakan companion object untuk menyimpan state kategori yang dipilih
    companion object {
        private var currentSelectedCategory: ImageCategory = ImageCategory.ALL
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val database = ImageDatabase.getDatabase(requireContext())

        // Buat ImageDataSource dengan database
        imageDataSource = ImageDataSource(requireContext(), database)

        // Inisialisasi database dengan data awal
        coroutineScope.launch {
            imageDataSource.initializeDatabase()
            loadImages(currentSelectedCategory)
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        // Set up category click listeners
        setupCategoryListeners(view)

        // Load images for the last selected category
        loadImages(currentSelectedCategory)

        // Set the background for the initially selected category
        updateCategoryBackground(view, currentSelectedCategory)

        return view
    }

    private fun setupCategoryListeners(view: View) {
        val allCategories = view.findViewById<View>(R.id.allCategories)
        val tourCategories = view.findViewById<View>(R.id.tour)
        val vehicleCategories = view.findViewById<View>(R.id.vehicle)
        val objectCategories = view.findViewById<View>(R.id.thing)
        val foodCategories = view.findViewById<View>(R.id.food)

        val categoryViews = listOf(
            allCategories to ImageCategory.ALL,
            tourCategories to ImageCategory.TOUR,
            vehicleCategories to ImageCategory.VEHICLE,
            objectCategories to ImageCategory.THING,
            foodCategories to ImageCategory.FOOD
        )

        categoryViews.forEach { (categoryView, category) ->
            categoryView.setOnClickListener {
                updateCategoryBackground(view, category)
                loadImages(category)
                currentSelectedCategory = category
            }
        }
    }

    private fun updateCategoryBackground(view: View, selectedCategory: ImageCategory) {
        val allCategories = view.findViewById<View>(R.id.allCategories)
        val tourCategories = view.findViewById<View>(R.id.tour)
        val vehicleCategories = view.findViewById<View>(R.id.vehicle)
        val objectCategories = view.findViewById<View>(R.id.thing)
        val foodCategories = view.findViewById<View>(R.id.food)

        // Reset semua kategori ke background tidak terpilih
        allCategories.setBackgroundResource(R.drawable.categories_bg_not_selected)
        tourCategories.setBackgroundResource(R.drawable.categories_bg_not_selected)
        vehicleCategories.setBackgroundResource(R.drawable.categories_bg_not_selected)
        objectCategories.setBackgroundResource(R.drawable.categories_bg_not_selected)
        foodCategories.setBackgroundResource(R.drawable.categories_bg_not_selected)

        // Set background kategori yang dipilih
        val selectedView = when (selectedCategory) {
            ImageCategory.ALL -> allCategories
            ImageCategory.TOUR -> tourCategories
            ImageCategory.VEHICLE -> vehicleCategories
            ImageCategory.THING -> objectCategories
            ImageCategory.FOOD -> foodCategories
        }
        selectedView.setBackgroundResource(R.drawable.categories_bg)
    }

    private fun loadImages(category: ImageCategory) {
        coroutineScope.launch {
            val images = imageDataSource.getImages(category).map { entity ->
                ImageItem(
                    id = entity.id,
                    title = entity.title,
                    description = entity.description,
                    imageUrl = entity.imageUrl,
                    category = ImageCategory.valueOf(entity.category)
                )
            }

            imageAdapter = ImageAdapter(images) { selectedImage ->
                val action = HomeFragmentDirections
                    .actionHomeFragmentToDetailActivity(selectedImage.id)
                findNavController().navigate(action)
            }
            recyclerView.adapter = imageAdapter
        }
    }

    // Tambahkan metode untuk mendapatkan kategori yang sedang dipilih
    fun getCurrentSelectedCategory(): ImageCategory {
        return currentSelectedCategory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Pastikan kategori yang dipilih sebelumnya tetap aktif
        updateCategoryBackground(view, currentSelectedCategory)
        loadImages(currentSelectedCategory)
    }
}

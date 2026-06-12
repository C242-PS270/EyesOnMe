package com.capstone.eyesonme.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imageDataSource: ImageDataSource

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        // Inisialisasi database
        val database = ImageDatabase.getDatabase(requireContext())
        imageDataSource = ImageDataSource(requireContext(), database)

        recyclerView = view.findViewById(R.id.rv_favorited)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(context, 2)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))

        imageAdapter = ImageAdapter(emptyList()) { selectedImage ->
            val action = FavoritesFragmentDirections
                .actionFavoritesFragmentToDetailActivity(selectedImage.id)
            findNavController().navigate(action)
        }
        recyclerView.adapter = imageAdapter

        return view
    }

    private fun loadFavoriteImages() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                progressBar.visibility = View.VISIBLE

                // Ambil gambar favorit dari database
                val favoriteEntities = withContext(Dispatchers.IO) {
                    imageDataSource.getFavoriteImages()
                }

                // Konversi dari ImageEntity ke ImageItem
                val favoriteImages = favoriteEntities.map { entity ->
                    ImageItem(
                        id = entity.id,
                        title = entity.title,
                        description = entity.description,
                        imageUrl = entity.imageUrl,
                        category = ImageCategory.valueOf(entity.category)
                    )
                }

                if (favoriteImages.isNotEmpty()) {
                    imageAdapter.updateData(favoriteImages)

                    // Sembunyikan pesan kosong jika ada gambar
                    view?.findViewById<TextView>(R.id.tv_empty_favorites)?.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    // Tampilkan pesan kosong jika tidak ada gambar favorit
                    view?.findViewById<TextView>(R.id.tv_empty_favorites)?.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("FavoritesFragment", "Error loading favorite images", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to load favorite images",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang data saat fragment ini terlihat kembali
        loadFavoriteImages()
    }
}

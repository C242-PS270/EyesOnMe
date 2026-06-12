package com.capstone.eyesonme.data

import android.content.Context
import com.capstone.eyesonme.R
import com.capstone.eyesonme.views.ImageCategory
import com.capstone.eyesonme.views.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ImageDataSource(
    private val context: Context,
    private val database: ImageDatabase
) {
    private val imageDao = database.imageDao()

    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        if (imageDao.getAllImages().isEmpty()) {
            val initialImages = listOf(
                // Tour Category
                ImageEntity(
                    id = "tour_1",
                    title = context.resources.getStringArray(R.array.tour)[0],
                    description = context.resources.getStringArray(R.array.tour_data_description)[0],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("tour_1_borobudur", "drawable", context.packageName)}",
                    category = ImageCategory.TOUR.name
                ),
                ImageEntity(
                    id = "tour_2",
                    title = context.resources.getStringArray(R.array.tour)[1],
                    description = context.resources.getStringArray(R.array.tour_data_description)[1],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("tour_2_pinkbeach", "drawable", context.packageName)}",
                    category = ImageCategory.TOUR.name
                ),
                ImageEntity(
                    id = "tour_3",
                    title = context.resources.getStringArray(R.array.tour)[2],
                    description = context.resources.getStringArray(R.array.tour_data_description)[2],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("tour_3_waykambas", "drawable", context.packageName)}",
                    category = ImageCategory.TOUR.name
                ),
                ImageEntity(
                    id = "tour_4",
                    title = context.resources.getStringArray(R.array.tour)[3],
                    description = context.resources.getStringArray(R.array.tour_data_description)[3],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("tour_4_rajaampat", "drawable", context.packageName)}",
                    category = ImageCategory.TOUR.name
                ),

                // Vehicle Category
                ImageEntity(
                    id = "vehicle_1",
                    title = context.resources.getStringArray(R.array.vehicle)[0],
                    description = context.resources.getStringArray(R.array.vehicle_data_description)[0],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("vehicle_1_tesla", "drawable", context.packageName)}",
                    category = ImageCategory.VEHICLE.name
                ),
                ImageEntity(
                    id = "vehicle_2",
                    title = context.resources.getStringArray(R.array.vehicle)[1],
                    description = context.resources.getStringArray(R.array.vehicle_data_description)[1],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("vehicle_2_maung", "drawable", context.packageName)}",
                    category = ImageCategory.VEHICLE.name
                ),
                ImageEntity(
                    id = "vehicle_3",
                    title = context.resources.getStringArray(R.array.vehicle)[2],
                    description = context.resources.getStringArray(R.array.vehicle_data_description)[2],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("vehicle_3_ehang", "drawable", context.packageName)}",
                    category = ImageCategory.VEHICLE.name
                ),
                ImageEntity(
                    id = "vehicle_4",
                    title = context.resources.getStringArray(R.array.vehicle)[3],
                    description = context.resources.getStringArray(R.array.vehicle_data_description)[3],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("vehicle_4_hoverboard", "drawable", context.packageName)}",
                    category = ImageCategory.VEHICLE.name
                ),

                // Fashion Category
                ImageEntity(
                    id = "fashion_1",
                    title = context.resources.getStringArray(R.array.fashion)[0],
                    description = context.resources.getStringArray(R.array.fashion_data_description)[0],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("fashion_1_summermen", "drawable", context.packageName)}",
                    category = ImageCategory.THING.name
                ),
                ImageEntity(
                    id = "fashion_2",
                    title = context.resources.getStringArray(R.array.fashion)[1],
                    description = context.resources.getStringArray(R.array.fashion_data_description)[1],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("fashion_2_wintermen", "drawable", context.packageName)}",
                    category = ImageCategory.THING.name
                ),
                ImageEntity(
                    id = "fashion_3",
                    title = context.resources.getStringArray(R.array.fashion)[2],
                    description = context.resources.getStringArray(R.array.fashion_data_description)[2],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("fashion_3_winterwomen", "drawable", context.packageName)}",
                    category = ImageCategory.THING.name
                ),
                ImageEntity(
                    id = "fashion_4",
                    title = context.resources.getStringArray(R.array.fashion)[3],
                    description = context.resources.getStringArray(R.array.fashion_data_description)[3],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("fashion_4_skenawomen", "drawable", context.packageName)}",
                    category = ImageCategory.THING.name
                ),

                // Food Category
                ImageEntity(
                    id = "food_1",
                    title = context.resources.getStringArray(R.array.food)[0],
                    description = context.resources.getStringArray(R.array.food_data_description)[0],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("food_1_tahugejrot", "drawable", context.packageName)}",
                    category = ImageCategory.FOOD.name
                ),
                ImageEntity(
                    id = "food_2",
                    title = context.resources.getStringArray(R.array.food)[1],
                    description = context.resources.getStringArray(R.array.food_data_description)[1],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("food_2_tapeketan", "drawable", context.packageName)}",
                    category = ImageCategory.FOOD.name
                ),
                ImageEntity(
                    id = "food_3",
                    title = context.resources.getStringArray(R.array.food)[2],
                    description = context.resources.getStringArray(R.array.food_data_description)[2],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("food_3_wedanguwuh", "drawable", context.packageName)}",
                    category = ImageCategory.FOOD.name
                ),
                ImageEntity(
                    id = "food_4",
                    title = context.resources.getStringArray(R.array.food)[3],
                    description = context.resources.getStringArray(R.array.food_data_description)[3],
                    imageUrl = "android.resource://${context.packageName}/${context.resources.getIdentifier("food_4_bungatelang", "drawable", context.packageName)}",
                    category = ImageCategory.FOOD.name
                )
            )
            imageDao.insertImages(initialImages)
        }
    }

    suspend fun getImages(category: ImageCategory = ImageCategory.ALL): List<ImageEntity> {
        return withContext(Dispatchers.IO) {
            when (category) {
                ImageCategory.ALL -> imageDao.getAllImages()
                else -> imageDao.getImagesByCategory(category.name)
            }
        }
    }

    suspend fun getImageById(id: String): ImageEntity? {
        return withContext(Dispatchers.IO) {
            imageDao.getImageById(id)
        }
    }

    suspend fun updateFavoriteStatus(imageId: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            imageDao.updateFavoriteStatus(imageId, isFavorite)
        }
    }

    suspend fun getFavoriteImages(): List<ImageEntity> {
        return withContext(Dispatchers.IO) {
            imageDao.getFavoriteImages()
        }
    }
}

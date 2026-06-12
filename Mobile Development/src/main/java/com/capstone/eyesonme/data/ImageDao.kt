package com.capstone.eyesonme.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.capstone.eyesonme.views.ImageEntity

@Dao
interface ImageDao {
    @Query("SELECT * FROM images")
    fun getAllImages(): List<ImageEntity>

    @Query("SELECT * FROM images WHERE category = :category")
    fun getImagesByCategory(category: String): List<ImageEntity>

    @Query("SELECT * FROM images WHERE id = :imageId")
    fun getImageById(imageId: String): ImageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImages(images: List<ImageEntity>)

    @Update
    fun updateImage(image: ImageEntity)

    @Query("UPDATE images SET is_favorite = :isFavorite WHERE id = :imageId")
    fun updateFavoriteStatus(imageId: String, isFavorite: Boolean)

    @Query("SELECT * FROM images WHERE is_Favorite = 1")
    suspend fun getFavoriteImages(): List<ImageEntity>
}
package com.capstone.eyesonme.views

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false
)
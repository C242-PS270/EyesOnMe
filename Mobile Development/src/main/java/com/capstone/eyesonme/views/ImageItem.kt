package com.capstone.eyesonme.views

data class ImageItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val category: ImageCategory
)

enum class ImageCategory {
    ALL,
    TOUR,
    VEHICLE,
    THING,
    FOOD
}


package com.capstone.eyesonme.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capstone.eyesonme.views.ImageEntity

@Database(entities = [ImageEntity::class], version = 1, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: ImageDatabase? = null

        fun getDatabase(context: Context): ImageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageDatabase::class.java,
                    "image_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
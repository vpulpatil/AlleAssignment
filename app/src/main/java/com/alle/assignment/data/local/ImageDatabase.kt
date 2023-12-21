package com.alle.assignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alle.assignment.domain.model.ImageEntity

@Database(
    entities = [ImageEntity::class],
    version = 1
)
@TypeConverters(ImageConverter::class)
abstract class ImageDatabase: RoomDatabase() {

    abstract val imageDao: ImageDao

    companion object {
        const val DATABASE_NAME = "images_db"
    }
}
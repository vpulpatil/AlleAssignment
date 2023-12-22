package com.alle.assignment.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.alle.assignment.domain.model.ImageCollections
import com.alle.assignment.domain.model.ImageDescription
import com.alle.assignment.domain.model.ImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImageDao {

    @Query("SELECT * FROM image")
    fun getNotes(): Flow<List<ImageEntity>>

    @Query("SELECT * FROM image WHERE imageName = :imageName")
    suspend fun getImageModelByImageName(imageName: String): ImageEntity?

    @Update
    suspend fun updateImageEntity(imageEntity: ImageEntity)

    @Upsert(entity = ImageEntity::class)
    suspend fun upsertImageDescription(imageDescription: ImageDescription)

    @Upsert(entity = ImageEntity::class)
    suspend fun upsertImageCollection(imageCollections: ImageCollections)

    @Delete
    suspend fun deleteImage(imageEntity: ImageEntity)
}
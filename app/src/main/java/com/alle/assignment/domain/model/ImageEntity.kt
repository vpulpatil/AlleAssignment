package com.alle.assignment.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image")
data class ImageEntity(
    @PrimaryKey val imageName: String,
    @ColumnInfo(defaultValue = "")
    val description: String,
    @ColumnInfo(defaultValue = "")
    var note: String,
    @ColumnInfo(defaultValue = "")
    var collections: List<String>,
    @ColumnInfo(defaultValue = "")
    var inactiveCollections: List<String>,
)

data class ImageDescription(
    val imageName: String,
    val description: String
)

data class ImageCollections(
    val imageName: String,
    val collections: List<String>,
)
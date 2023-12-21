package com.alle.assignment.data.local

import androidx.room.TypeConverter

class ImageConverter {
    val STRING_SEPARATOR = ","

    @TypeConverter
    fun List<String>.toStringData() = this.joinToString(STRING_SEPARATOR)

    @TypeConverter
    fun String.toList() = this.split(STRING_SEPARATOR)
}
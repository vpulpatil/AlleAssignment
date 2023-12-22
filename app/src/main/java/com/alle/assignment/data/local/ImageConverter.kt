package com.alle.assignment.data.local

import androidx.room.TypeConverter

class ImageConverter {
    val STRING_SEPARATOR = ","

    @TypeConverter
    fun List<String>.toStringData() = this.joinToString(STRING_SEPARATOR)

    @TypeConverter
    fun String.toList(): List<String> {
        return if (this.isEmpty()) {
            emptyList()
        } else {
            this.split(STRING_SEPARATOR)
        }
    }
}
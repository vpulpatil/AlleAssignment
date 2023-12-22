package com.alle.assignment.di

import android.app.Application
import androidx.room.Room
import com.alle.assignment.data.local.ImageDao
import com.alle.assignment.data.local.ImageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideImageDatabase(app: Application): ImageDatabase {
        return Room.databaseBuilder(
            app,
            ImageDatabase::class.java,
            ImageDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageDao(imageDatabase: ImageDatabase): ImageDao {
        return imageDatabase.imageDao
    }
}
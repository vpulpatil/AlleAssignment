package com.alle.assignment.di

import com.alle.assignment.data.repository.TextRecognizerRepository
import com.alle.assignment.domain.repository.TextRecognizerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTextRecognizerRepository(
        repository: TextRecognizerRepositoryImpl
    ): TextRecognizerRepository
}
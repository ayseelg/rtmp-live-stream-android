package com.example.rtmpstreamingproject.di

import com.example.rtmplibrary.data.repository.StreamRepositoryImpl
import com.example.rtmplibrary.domain.repository.StreamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StreamModule {

    @Binds
    @Singleton
    abstract fun bindStreamRepository(impl: StreamRepositoryImpl): StreamRepository
}

package com.digitalcash.soarapoc.di

import com.digitalcash.soarapoc.data.remote.data_source.FusedLocationClientLocationGetter
import com.digitalcash.soarapoc.data.remote.data_source.LocationGetter
import com.digitalcash.soarapoc.data.repository.LocationRepoImpl
import com.digitalcash.soarapoc.data.repository.MapRepositoryImpl
import com.digitalcash.soarapoc.data.repository.WebSocketRepositoryImpl
import com.digitalcash.soarapoc.domain.repository.LocationRepo
import com.digitalcash.soarapoc.domain.repository.MapRepository
import com.digitalcash.soarapoc.domain.repository.WebSocketRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideLocationGetter(
        fusedLocationClientLocationGetter: FusedLocationClientLocationGetter
    ): LocationGetter


    @Binds
    @Singleton
    abstract fun provideLocationRepo(
        locationRepoImpl: LocationRepoImpl
    ): LocationRepo

    @Binds
    @Singleton
    abstract fun provideMapRepository(
        mapRepositoryImpl: MapRepositoryImpl
    ): MapRepository


    @Binds
    @Singleton
    abstract fun provideWebSocketRepository(webSocketRepositoryImpl: WebSocketRepositoryImpl): WebSocketRepository
}
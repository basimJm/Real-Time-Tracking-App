package com.digitalcash.soarapoc.di

import com.digitalcash.soarapoc.data.repository.WebSocketRepositoryImpl
import com.digitalcash.soarapoc.domain.repository.WebSocketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideWebSocketRepository(httpClient: OkHttpClient): WebSocketRepository {
        return WebSocketRepositoryImpl(httpClient)
    }
}
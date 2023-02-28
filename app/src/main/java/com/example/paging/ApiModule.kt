package com.example.paging


import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    fun create(): MyApiClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC
        val baseUrl = "https://quotable.io/"
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MyApiClient::class.java)
    }

    @Singleton
    @Provides
    fun provideMoviesDao(authorsDataBase: AuthorsDataBase): AuthorDao = authorsDataBase.authorDao()


    @Singleton
    @Provides
    fun provideRemoteKeysDao(authorsDataBase: AuthorsDataBase): RemoteKeysDao =
        authorsDataBase.getRemoteKeysDao()

}
package com.gsench.senchenok.kinopoisk_api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {
    @GET("api/v2.2/films/top?type=TOP_100_POPULAR_FILMS")
    suspend fun getTop100Movies(
        @Header("x-api-key") token: String,
        @Query("page") page: Int = 1
    ): KinopoiskMoviesList

    @GET("api/v2.2/films/{id}")
    suspend fun getMovieDetails(
        @Header("x-api-key") token: String,
        @Path("id") id: Int
    ): KinopoiskMovieDetails

}

fun instantiateKinopoiskApi(): KinopoiskApi {
    val httpInterceptor = HttpLoggingInterceptor()
    httpInterceptor.level = HttpLoggingInterceptor.Level.BODY
    val httpClient = OkHttpClient.Builder()
        .addInterceptor(httpInterceptor)
        .build()
    val retrofit = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("https://kinopoiskapiunofficial.tech")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(KinopoiskApi::class.java)
}

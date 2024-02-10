package com.gsench.senchenok.kinopoisk_api

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


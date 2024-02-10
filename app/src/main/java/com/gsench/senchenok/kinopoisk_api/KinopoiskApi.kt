package com.gsench.senchenok.kinopoisk_api

import retrofit2.http.GET
import retrofit2.http.Header

interface KinopoiskApi {
    @GET("api/v2.2/films/top?type=TOP_100_POPULAR_FILMS")
    suspend fun getTop100Movies(@Header("x-api-key") token: String): KinopoiskMoviesList

}
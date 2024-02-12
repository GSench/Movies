package com.gsench.senchenok.domain

import com.gsench.senchenok.KINOPOISK_TOKEN
import com.gsench.senchenok.domain.network.LoadResult
import com.gsench.senchenok.domain.kinopoisk_api.KinopoiskApi
import java.net.ConnectException

class MovieRepository(private val api: KinopoiskApi) {

    private suspend fun<T> getApiResponse(apiCall: suspend () -> T): LoadResult<T> =
        try {
            val response = apiCall()
            LoadResult.Success(response)
        } catch (e: Throwable) {
            if (e is ConnectException) {
                LoadResult.NoConnection(e)
            } else {
                LoadResult.Error(e)
            }
        }

    suspend fun getTop100Movies(page: Int = 1) = getApiResponse {
        api.getTop100Movies(KINOPOISK_TOKEN, page)
    }

    suspend fun getMovieDetails(id: Int) = getApiResponse {
        api.getMovieDetails(KINOPOISK_TOKEN, id)
    }

}
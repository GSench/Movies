package com.gsench.senchenok

import android.app.Application
import com.gsench.senchenok.domain.MovieRepository
import com.gsench.senchenok.domain.kinopoisk_api.KinopoiskApi
import com.gsench.senchenok.domain.network.Network

class MyApplication: Application() {

    lateinit var network: Network
    lateinit var repository: MovieRepository
    lateinit var kinopoiskApi: KinopoiskApi
    override fun onCreate() {
        super.onCreate()
        network = Network(this)
        kinopoiskApi = KinopoiskApi.instantiateKinopoiskApi(network.httpClient)
        repository = MovieRepository(kinopoiskApi)
    }

}
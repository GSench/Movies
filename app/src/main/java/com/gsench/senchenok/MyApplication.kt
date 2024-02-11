package com.gsench.senchenok

import android.app.Application
import com.gsench.senchenok.data.network.HttpClient

class MyApplication: Application() {

    lateinit var httpClient: HttpClient
    override fun onCreate() {
        super.onCreate()
        httpClient = HttpClient(this)
    }

}
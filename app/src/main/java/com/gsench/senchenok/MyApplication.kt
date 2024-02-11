package com.gsench.senchenok

import android.app.Application
import com.gsench.senchenok.data.network.Network

class MyApplication: Application() {

    lateinit var network: Network
    override fun onCreate() {
        super.onCreate()
        network = Network(this)
    }

}
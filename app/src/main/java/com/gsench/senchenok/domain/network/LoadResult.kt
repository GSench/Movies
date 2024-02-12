package com.gsench.senchenok.domain.network

import java.net.ConnectException

sealed class LoadResult<out T> {

    class Success<out T>(val data: T) : LoadResult<T>()
    class Error(val exception: Throwable) : LoadResult<Nothing>()
    class NoConnection(val exception: ConnectException) : LoadResult<Nothing>()

}
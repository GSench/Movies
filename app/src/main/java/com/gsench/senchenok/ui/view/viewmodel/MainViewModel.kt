package com.gsench.senchenok.ui.view.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.gsench.senchenok.MyApplication
import com.gsench.senchenok.domain.MovieRepository
import com.gsench.senchenok.domain.kinopoisk_api.KinopoiskApiValues
import com.gsench.senchenok.domain.network.LoadResult
import com.gsench.senchenok.ui.view.UIEvent
import com.gsench.senchenok.ui.view.model.MovieListItem
import com.gsench.senchenok.ui.view.model.mapping.toMovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel (
    private val repository: MovieRepository
) : ViewModel() {

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return MainViewModel((application as MyApplication).repository) as T
            }
        }
    }

    val loadingStateFlow = MutableLiveData(LoadingState.IDLE)
    val moviesList = MutableLiveData<MutableList<MovieListItem>>(mutableListOf())

    private var page = KinopoiskApiValues.INITIAL_PAGE - 1
    private var totalPages = 1

    private val pageLiveData = MutableLiveData(page)
    private val totalPagesLiveData = MutableLiveData(totalPages)

    @OptIn(FlowPreview::class)
    fun subscribeUI(uiEventFlow: Flow<UIEvent>) = uiEventFlow
        .debounce(10)
        .onEach {
            when(it) {
                UIEvent.SCROLLED_TO_BOTTOM -> onListScrolledToBottom()
                UIEvent.POPULAR_LIST_OPENED -> onListScrolledToBottom()
                UIEvent.RETRY -> onListScrolledToBottom()
            }
        }
        .launchIn(CoroutineScope(Dispatchers.IO))

    private suspend fun onListScrolledToBottom() {
        if(page >= totalPages) return
        loadingStateFlow.postValue(LoadingState.LOADING)
        Log.d("LOADING LIST FLOW", "page.value = ${page+1}")
        when(val loadResult = repository.getTop100Movies(page+1)){
            is LoadResult.Success -> {
                incrementPage(loadResult.data.pagesCount)
                val appendMoviesList = loadResult.data.films.map { it.toMovieViewModel() }
                moviesList.postValue((moviesList.value ?: mutableListOf()).apply { addAll(appendMoviesList) })
                loadingStateFlow.postValue(LoadingState.IDLE)
            }
            is LoadResult.NoConnection -> {
                loadingStateFlow.postValue(LoadingState.INTERNET_ERROR)
            }
            is LoadResult.Error -> {
                loadingStateFlow.postValue(LoadingState.ERROR)
            }
        }
    }

    private fun incrementPage(newTotalPages: Int) {
        page++
        totalPages = newTotalPages
        pageLiveData.postValue(page)
        totalPagesLiveData.postValue(totalPages)
    }

}

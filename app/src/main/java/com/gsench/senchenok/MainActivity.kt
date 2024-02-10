package com.gsench.senchenok

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.gsench.senchenok.databinding.ActivityMainBinding
import com.gsench.senchenok.kinopoisk_api.KinopoiskApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var kinopoiskApi: KinopoiskApi
    private lateinit var movieListAdapter: MovieListAdapter

    private var totalPagesCount = 1
    private var lastMovieListPageLoaded = 0
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainToolbar.title = getString(R.string.popular_title)
        setSupportActionBar(binding.mainToolbar)
        movieListAdapter = MovieListAdapter(this)
        val movieLayoutManager = LinearLayoutManager(this)
        with(binding.movieListView) {
            layoutManager = movieLayoutManager
            adapter = movieListAdapter
        }
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
        kinopoiskApi = retrofit.create(KinopoiskApi::class.java)

        binding.movieListView.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItem = movieLayoutManager.findLastVisibleItemPosition()
                Log.d("RECYCLER_VIEW", "lastVisibleItem = ${lastVisibleItem}")
                if(lastVisibleItem > movieListAdapter.itemCount - 6) loadNewPage()
            }
        })

        loadNewPage()
    }

    private fun loadNewPage() {
        if(lastMovieListPageLoaded < totalPagesCount && !isLoading) {
            isLoading = true
            CoroutineScope(Dispatchers.IO).launch {
                val kinopoiskMoviesList = kinopoiskApi.getTop100Movies(
                    token = "e30ffed0-76ab-4dd6-b41f-4c9da2b2735b",
                    page = ++lastMovieListPageLoaded
                )
                runOnUiThread {
                    totalPagesCount = kinopoiskMoviesList.pagesCount
                    movieListAdapter.appendMovies(kinopoiskMoviesList
                        .films
                        .map { kinopoiskMovie ->
                            MovieListItem(
                                title = arrayOf(
                                    kinopoiskMovie.nameRu,
                                    kinopoiskMovie.nameOriginal,
                                    kinopoiskMovie.nameEn,
                                    "???"
                                    ).firstNotNullOf {it},
                                genre = kinopoiskMovie
                                    .genres
                                    .take(3).joinToString { genreObj ->
                                        genreObj.genre.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.getDefault()
                                            ) else it.toString()
                                        }
                                    } ?:"???",
                                year = kinopoiskMovie.year?:"???",
                                iconUrl = kinopoiskMovie.posterUrlPreview?:"???"
                            ) }
                    )
                    isLoading = false
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

}
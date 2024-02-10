package com.gsench.senchenok

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.mainToolbar.title = getString(R.string.popular_title)
        setSupportActionBar(binding.mainToolbar)
        val movieListAdapter = MovieListAdapter(this)
        with(binding.movieListView) {
            layoutManager = LinearLayoutManager(this@MainActivity)
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
        val kinopoiskApi = retrofit.create(KinopoiskApi::class.java)
        CoroutineScope(Dispatchers.IO).launch {
            val kinopoiskMoviesList = kinopoiskApi.getTop100Movies("e30ffed0-76ab-4dd6-b41f-4c9da2b2735b")
            runOnUiThread {
                movieListAdapter.movieList = kinopoiskMoviesList
                    .films
                    .map { kinopoiskMovie ->
                        MovieListItem(
                        title = kinopoiskMovie.nameRu ?: "",
                        genre = kinopoiskMovie
                            .genres
                            .take(3)
                            .map{ genreObj ->
                                genreObj.genre.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(
                                        Locale.getDefault()
                                    ) else it.toString()
                            } }
                            .joinToString(),
                        year = kinopoiskMovie.year ?: "????",
                        iconUrl = kinopoiskMovie.posterUrlPreview ?: ""
                    ) }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

}
package com.gsench.senchenok

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.gsench.senchenok.databinding.FragmentMovieBinding
import com.gsench.senchenok.kinopoisk_api.KinopoiskApi
import com.gsench.senchenok.kinopoisk_api.instantiateKinopoiskApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MovieFragment: Fragment() {

    private lateinit var binding: FragmentMovieBinding
    private lateinit var kinopoiskApi: KinopoiskApi

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieBinding.inflate(layoutInflater)
        kinopoiskApi = instantiateKinopoiskApi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val kinopoiskFilmId = arguments?.getInt(KINOPOISK_FILM_ID, 0) ?: return
        Log.d("MOVIE_FRAGMENT", "kinopoiskFilmId = $kinopoiskFilmId")
        loadMovieDetails(kinopoiskFilmId)
    }

    private fun loadMovieDetails(kinopoiskFilmId: Int) {
        uiScope.launch(Dispatchers.IO){
            val kinopoiskMovieDetails = kinopoiskApi.getMovieDetails(KINOPOISK_TOKEN, kinopoiskFilmId)
            withContext(Dispatchers.Main){
                with(binding) {
                    movieTitle.text = arrayOf(
                        kinopoiskMovieDetails.nameRu,
                        kinopoiskMovieDetails.nameOriginal,
                        kinopoiskMovieDetails.nameEn,
                        "???"
                    ).firstNotNullOf { it }
                    movieDescription.text = kinopoiskMovieDetails.description ?: ""
                    movieGenre.text = kinopoiskMovieDetails.genres?.joinToString { genreObj ->
                        genreObj.genre.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    } ?: "???"
                    movieCountry.text = kinopoiskMovieDetails.countries?.joinToString { countriesObj ->
                        countriesObj.country.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    }
                    Glide
                        .with(this@MovieFragment)
                        .load(kinopoiskMovieDetails.posterUrl)
                        .into(moviePoster)
                }
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    companion object {
        const val MOVIE_FRAGMENT_ID = "MOVIE_FRAGMENT_ID"
        const val KINOPOISK_FILM_ID = "KINOPOISK_FILM_ID"
        fun newInstance(kinopoiskFilmId: Int): MovieFragment {
            val movieFragment = MovieFragment()
            val args = Bundle()
            args.putInt(KINOPOISK_FILM_ID, kinopoiskFilmId)
            movieFragment.arguments = args
            return movieFragment
        }
    }

}
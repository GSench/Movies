package com.gsench.senchenok.ui.view.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.gsench.senchenok.MyApplication
import com.gsench.senchenok.databinding.FragmentMovieBinding
import com.gsench.senchenok.domain.MovieRepository
import com.gsench.senchenok.domain.network.LoadResult
import com.gsench.senchenok.ui.view.model.MovieDetails
import com.gsench.senchenok.ui.view.model.mapping.toMovieDetailsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieFragment: Fragment() {

    private lateinit var binding: FragmentMovieBinding
    private lateinit var repository: MovieRepository

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

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieBinding.inflate(layoutInflater)
        repository = (activity?.application as MyApplication).repository
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val kinopoiskFilmId = arguments?.getInt(KINOPOISK_FILM_ID, 0) ?: return
        Log.d("MOVIE_FRAGMENT", "kinopoiskFilmId = $kinopoiskFilmId")
        loadMovieDetails(kinopoiskFilmId)
    }

    private fun loadMovieDetails(kinopoiskFilmId: Int) {
        showMovieContentLoading()
        showMoviePosterLoading()
        uiScope.launch(Dispatchers.IO){
            val movieDetails = repository.getMovieDetails(kinopoiskFilmId)
            when(movieDetails) {
                is LoadResult.Error -> withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                }
                is LoadResult.NoConnection -> withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Internet Error", Toast.LENGTH_LONG).show()
                }
                is LoadResult.Success -> withContext(Dispatchers.Main) {
                    setMovieDetails(movieDetails.data.toMovieDetailsViewModel())
                    hideMovieContentLoading()
                }
            }
        }
    }

    private fun setMovieDetails(movieDetails: MovieDetails) {
        with(binding) {
            movieTitle.text = movieDetails.title
            movieDescription.text = movieDetails.description
            movieGenre.text = movieDetails.genres
            movieCountry.text = movieDetails.countries
            showMoviePosterLoading()
            Glide
                .with(this@MovieFragment)
                .load(movieDetails.posterUrl)
                .addListener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        hideMoviePosterLoading()
                        return false
                    }
                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(moviePoster)
        }
    }

    private fun showMovieContentLoading() = with(binding) {
        movieDetailsContent.visibility = View.GONE
        moviePosterPlaceholder.startShimmer()
        movieDetailsPlaceholder.startShimmer()
    }

    private fun showMoviePosterLoading() = with(binding) {
        moviePosterPlaceholder.visibility = View.VISIBLE
        moviePosterPlaceholder.startShimmer()
    }

    private fun hideMovieContentLoading() = with(binding) {
        movieDetailsPlaceholder.stopShimmer()
        movieDetailsPlaceholder.visibility = View.GONE
        movieDetailsContent.visibility = View.VISIBLE
    }

    private fun hideMoviePosterLoading() = with(binding) {
        moviePosterPlaceholder.stopShimmer()
        moviePosterPlaceholder.visibility = View.GONE
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}
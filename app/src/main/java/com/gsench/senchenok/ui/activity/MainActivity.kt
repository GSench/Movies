package com.gsench.senchenok.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.gsench.senchenok.KINOPOISK_TOKEN
import com.gsench.senchenok.MyApplication
import com.gsench.senchenok.ui.fragment.MovieFragment
import com.gsench.senchenok.ui.adapter.MovieListAdapter
import com.gsench.senchenok.ui.adapter.MovieListFooterAdapter
import com.gsench.senchenok.R
import com.gsench.senchenok.data.network.Network
import com.gsench.senchenok.databinding.ActivityMainBinding
import com.gsench.senchenok.kinopoisk_api.KinopoiskApi
import com.gsench.senchenok.kinopoisk_api.instantiateKinopoiskApi
import com.gsench.senchenok.ui.viewmodel.mapping.toMovieViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var network: Network
    private lateinit var kinopoiskApi: KinopoiskApi
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var movieListFooterAdapter: MovieListFooterAdapter

    private var totalPagesCount = 1
    private var lastMovieListPageLoaded = 0
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupMovieList()
        setupNetwork()
        loadNewPage()
    }

    private fun setupNetwork() {
        network = (application as MyApplication).network
        kinopoiskApi = instantiateKinopoiskApi(network.httpClient)
    }

    private fun setupToolbar() {
        binding.mainToolbar.title = getString(R.string.popular_title)
        setSupportActionBar(binding.mainToolbar)
    }

    private fun setupMovieList() {
        movieListAdapter = MovieListAdapter(this, ::onMovieClick)
        val movieLayoutManager = LinearLayoutManager(this)

        movieListFooterAdapter = MovieListFooterAdapter()
        val concatAdapter = ConcatAdapter(movieListAdapter, movieListFooterAdapter)

        with(binding.movieListView) {
            layoutManager = movieLayoutManager
            adapter = concatAdapter
            addOnScrollListener(object: OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItem = movieLayoutManager.findLastVisibleItemPosition()
                    if(lastVisibleItem > movieListAdapter.itemCount - 6) loadNewPage()
                }
            })
        }
    }

    private fun onMovieClick(id: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        val movieFragment = MovieFragment.newInstance(id)
        supportFragmentManager.popBackStack()
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.slide_in,
            R.anim.fade_out,
        )
        transaction.addToBackStack(MovieFragment.MOVIE_FRAGMENT_ID)
        transaction.replace(R.id.movie_details_fragment_view, movieFragment)
        transaction.commit()
    }

    private fun loadNewPage() {
        if(lastMovieListPageLoaded < totalPagesCount && !isLoading) {
            isLoading = true
            showLoading()
            CoroutineScope(Dispatchers.IO).launch {
                val kinopoiskMoviesList = kinopoiskApi.getTop100Movies(
                    token = KINOPOISK_TOKEN,
                    page = ++lastMovieListPageLoaded
                )
                runOnUiThread {
                    totalPagesCount = kinopoiskMoviesList.pagesCount
                    movieListAdapter.appendMovies(kinopoiskMoviesList
                        .films
                        .map { it.toMovieViewModel() }
                    )
                    isLoading = false
                    hideLoading()
                }
            }
        }
    }

    private fun showLoading() {
        if(lastMovieListPageLoaded==0){
            binding.progressBar.visibility = View.VISIBLE
            movieListFooterAdapter.hideLoading()
        } else {
            binding.progressBar.visibility = View.GONE
            movieListFooterAdapter.showLoading()
        }
    }

    private fun hideLoading() {
        movieListFooterAdapter.hideLoading()
        binding.progressBar.visibility = View.GONE
    }

}
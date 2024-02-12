package com.gsench.senchenok.ui.view.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.gsench.senchenok.R
import com.gsench.senchenok.databinding.ActivityMainBinding
import com.gsench.senchenok.ui.view.UIEvent
import com.gsench.senchenok.ui.view.adapter.MovieListAdapter
import com.gsench.senchenok.ui.view.adapter.MovieListFooterAdapter
import com.gsench.senchenok.ui.view.fragment.MovieFragment
import com.gsench.senchenok.ui.view.viewmodel.LoadingState
import com.gsench.senchenok.ui.view.viewmodel.MainViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels{MainViewModel.Factory}
    private lateinit var movieListAdapter: MovieListAdapter
    private lateinit var movieListFooterAdapter: MovieListFooterAdapter
    private lateinit var uiEventsFlow: Flow<UIEvent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
        setupMovieList()
        observeUI()
        observeViewModel()
        viewModel.subscribeUI(uiEventsFlow)
    }

    private fun setupToolbar() {
        binding.mainToolbar.title = getString(R.string.popular_title)
        setSupportActionBar(binding.mainToolbar)
    }

    private fun setupMovieList() {
        movieListAdapter = MovieListAdapter(::onMovieClick)
        val movieLayoutManager = LinearLayoutManager(this)

        movieListFooterAdapter = MovieListFooterAdapter()
        val concatAdapter = ConcatAdapter(movieListAdapter, movieListFooterAdapter)

        with(binding.movieListView) {
            layoutManager = movieLayoutManager
            adapter = concatAdapter
        }
    }

    fun observeUI() {
        uiEventsFlow = callbackFlow {
            binding.movieListView.addOnScrollListener(object: OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItem = (binding.movieListView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if(lastVisibleItem > movieListAdapter.itemCount - 6) {
                        trySendBlocking(UIEvent.SCROLLED_TO_BOTTOM)
                    }
                }
            })
            binding.retryButton.setOnClickListener(object: OnClickListener {
                override fun onClick(v: View?) {
                    trySendBlocking(UIEvent.RETRY)
                }
            })
            trySendBlocking(UIEvent.POPULAR_LIST_OPENED)
            awaitClose()
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

    private fun observeViewModel() {
        viewModel.loadingStateFlow.observe(this) {
            if(it == null) return@observe
            when(it) {
                LoadingState.IDLE -> {
                    hideLoading()
                    hideError()
                }
                LoadingState.LOADING -> {
                    showLoading()
                    hideError()
                }
                LoadingState.ERROR -> {
                    hideLoading()
                    showError()
                }
                LoadingState.INTERNET_ERROR -> {
                    hideLoading()
                    showError()
                }
            }
        }
        viewModel.moviesList.observe(this) {
            if(it == null) return@observe
            movieListAdapter.submitList(it)
        }
    }

    private fun showLoading() {
        if(false){
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

    private fun showError() {
        binding.loadingErrorView.visibility = View.VISIBLE
    }

    private fun hideError() {
        binding.loadingErrorView.visibility = View.GONE
    }

}
package com.gsench.senchenok

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.gsench.senchenok.databinding.ActivityMainBinding

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
        movieListAdapter.movieList = listOf(
            MovieListItem("The Avengers", "Fantasy", "2012", "http://kinopoiskapiunofficial.tech/images/posters/kp/263531.jpg")
        )
    }

}
package com.gsench.senchenok

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gsench.senchenok.databinding.MovieListItemBinding
import com.gsench.senchenok.ui.elements.getShimmerDrawable

class MovieListAdapter(
    val context: Context,
    val onMovieClick: (movieID: Int) -> Unit
) : RecyclerView.Adapter<MovieListAdapter.MovieListItemViewHolder>() {

    private val movieList: MutableList<MovieListItem> = mutableListOf()

    fun appendMovies(movies: List<MovieListItem>) {
        val prevListSize = movieList.size
        movieList.addAll(movies)
        notifyItemRangeInserted(prevListSize, movies.size)
    }

    class MovieListItemViewHolder (val binding: MovieListItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieListItemBinding.inflate(inflater, parent, false)
        return MovieListItemViewHolder(binding)
    }

    override fun getItemCount() = movieList.size

    override fun onBindViewHolder(holder: MovieListItemViewHolder, position: Int) {
        with(holder.binding) {
            movieTitle.text = movieList[position].title
            movieSubtitle.text = context.getString(
                R.string.movie_item_subtitle_format,
                movieList[position].genre,
                movieList[position].year
            )
            Glide
                .with(context)
                .load(movieList[position].iconUrl)
                .placeholder(getShimmerDrawable())
                .into(movieIcon)
            movieCard.setOnClickListener { onMovieClick(movieList[position].id) }
        }
    }
}
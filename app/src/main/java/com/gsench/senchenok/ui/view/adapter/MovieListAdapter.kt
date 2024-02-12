package com.gsench.senchenok.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gsench.senchenok.R
import com.gsench.senchenok.databinding.MovieListItemBinding
import com.gsench.senchenok.ui.ui_elements.getShimmerDrawable
import com.gsench.senchenok.ui.view.model.MovieListItem

class MovieListAdapter(
    private val onMovieClick: (movieID: Int) -> Unit
) : ListAdapter<MovieListItem, MovieListAdapter.MovieListItemViewHolder>(MovieListDiffUtil()) {

    class MovieListItemViewHolder (val binding: MovieListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieListItem, onMovieClick: (movieID: Int) -> Unit) = with(binding) {
            movieTitle.text = movie.title
            movieSubtitle.text = movieSubtitle.context.getString(
                R.string.movie_item_subtitle_format,
                movie.genre,
                movie.year
            )
            Glide
                .with(movieIcon.context)
                .load(movie.iconUrl)
                .placeholder(getShimmerDrawable())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(movieIcon)
            movieCard.setOnClickListener { onMovieClick(movie.id) }
        }
    }
    class MovieListDiffUtil : DiffUtil.ItemCallback<MovieListItem>() {
        override fun areItemsTheSame(oldItem: MovieListItem, newItem: MovieListItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MovieListItem, newItem: MovieListItem) = oldItem.id == newItem.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieListItemBinding.inflate(inflater, parent, false)
        return MovieListItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieListItemViewHolder, position: Int) =
        holder.bind(currentList[position], onMovieClick)

    override fun submitList(list: List<MovieListItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

}
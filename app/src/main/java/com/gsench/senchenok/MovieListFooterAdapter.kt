package com.gsench.senchenok

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gsench.senchenok.databinding.MovieListFooterBinding

class MovieListFooterAdapter : RecyclerView.Adapter<MovieListFooterAdapter.MovieListFooterViewHolder>() {

    private var isVisible = false
    class MovieListFooterViewHolder (binding: MovieListFooterBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListFooterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieListFooterBinding.inflate(inflater, parent, false)
        return MovieListFooterViewHolder(binding)
    }

    fun showLoading() {
        isVisible = true
        notifyItemInserted(0)
    }

    fun hideLoading(){
        isVisible = false
        notifyItemRemoved(0)
    }

    override fun getItemCount() = if(isVisible) 1 else 0

    override fun onBindViewHolder(holder: MovieListFooterViewHolder, position: Int) {

    }
}
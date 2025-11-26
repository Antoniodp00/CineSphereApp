package com.proyecto.cinesphereapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.model.MovieDto

class MovieAdapter(
    private var movies: List<MovieDto>,
    private val onMovieClick: (MovieDto) -> Unit // Lambda para manejar clics
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvTitle: TextView = view.findViewById(R.id.tvMovieTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]

        holder.tvTitle.text = movie.title
        holder.tvRating.text = "★ ${movie.rating}"

        // Cargar imagen con Glide
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // Imagen mientras carga
            .into(holder.ivPoster)

        // Evento click
        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<MovieDto>) {
        movies = newMovies
        notifyDataSetChanged()
    }
}
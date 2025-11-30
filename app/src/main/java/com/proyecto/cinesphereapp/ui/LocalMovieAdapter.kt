package com.proyecto.cinesphereapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.model.PeliculaLocal

class LocalMovieAdapter(
    movies: List<PeliculaLocal>,
    private val onMovieClick: (PeliculaLocal) -> Unit
) : RecyclerView.Adapter<LocalMovieAdapter.ViewHolder>() {

    private val items = mutableListOf<PeliculaLocal>().apply { addAll(movies) }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvTitle: TextView = view.findViewById(R.id.tvMovieTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Reusamos el diseño 'item_movie.xml' que ya creaste
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = items[position]

        holder.tvTitle.text = movie.titulo
        // En lugar de estrellas, mostramos el estado
        holder.tvRating.text = movie.estado

        // Construimos la URL completa porque en BD solo guardamos la ruta parcial
        val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivPoster)

        holder.itemView.setOnClickListener {
            onMovieClick(movie)
        }
    }

    fun setList(newList: List<PeliculaLocal>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun addMovies(newItems: List<PeliculaLocal>) {
        if (newItems.isEmpty()) return
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    fun getItems(): List<PeliculaLocal> = items

    override fun getItemCount() = items.size
}
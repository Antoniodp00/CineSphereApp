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
import java.lang.String

/**
 * Adaptador para el RecyclerView que muestra una lista de películas.
 * @param movies La lista inicial de películas a mostrar.
 * @param onMovieClick Una función lambda que se ejecuta cuando se hace clic en una película.
 */
class MovieAdapter(
    movies: List<MovieDto>,
    private val onMovieClick: (MovieDto) -> Unit // Lambda para manejar clics
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val items = mutableListOf<MovieDto>().apply { addAll(movies) }

    /**
     * ViewHolder para cada elemento de la lista de películas.
     * @param view La vista del elemento.
     */
    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvTitle: TextView = view.findViewById(R.id.tvMovieTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    /**
     * Se llama cuando RecyclerView necesita un nuevo [MovieViewHolder] del tipo dado para representar un elemento.
     * @param parent El ViewGroup en el que se agregará la nueva vista después de que se vincule a una posición de adaptador.
     * @param viewType El tipo de vista de la nueva Vista.
     * @return Un nuevo MovieViewHolder que contiene una Vista del tipo de vista dado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    /**
     * Se llama por RecyclerView para mostrar los datos en la posición especificada.
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = items[position]

        holder.tvTitle.text = movie.title
        holder.tvRating.text = String.format("★ %.1f", movie.rating)

        if (movie.posterPath.isNullOrBlank()) {
            holder.ivPoster.setImageResource(android.R.drawable.ic_menu_gallery)
        } else {
            val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.ivPoster)
        }

        // Evento click
        holder.itemView.setOnClickListener { onMovieClick(movie) }
    }

    /**
     * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
     * @return El número total de elementos en este adaptador.
     */
    override fun getItemCount() = items.size

    /**
     * Establece una nueva lista de películas en el adaptador, borrando las anteriores.
     * @param newMovies La nueva lista de películas.
     */
    fun setMovies(newMovies: List<MovieDto>) {
        items.clear()
        items.addAll(newMovies)
        notifyDataSetChanged()
    }

    /**
     * Agrega una lista de películas al final de la lista actual.
     * @param newMovies La lista de nuevas películas a agregar.
     */
    fun addMovies(newMovies: List<MovieDto>) {
        if (newMovies.isEmpty()) return
        val start = items.size
        items.addAll(newMovies)
        notifyItemRangeInserted(start, newMovies.size)
    }

    /**
     * Obtiene la lista actual de películas en el adaptador.
     * @return La lista de películas.
     */
    fun getItems(): List<MovieDto> = items
}
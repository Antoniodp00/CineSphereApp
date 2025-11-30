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

/**
 * Adaptador para el RecyclerView que muestra una lista de películas locales (guardadas por el usuario).
 * @param movies La lista inicial de películas locales a mostrar.
 * @param onMovieClick Una función lambda que se ejecuta cuando se hace clic en una película.
 */
class LocalMovieAdapter(
    movies: List<PeliculaLocal>,
    private val onMovieClick: (PeliculaLocal) -> Unit
) : RecyclerView.Adapter<LocalMovieAdapter.ViewHolder>() {

    private val items = mutableListOf<PeliculaLocal>().apply { addAll(movies) }

    /**
     * ViewHolder para cada elemento de la lista de películas locales.
     * @param view La vista del elemento.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPoster: ImageView = view.findViewById(R.id.ivPoster)
        val tvTitle: TextView = view.findViewById(R.id.tvMovieTitle)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    /**
     * Se llama cuando RecyclerView necesita un nuevo [ViewHolder] del tipo dado para representar un elemento.
     * @param parent El ViewGroup en el que se agregará la nueva vista después de que se vincule a una posición de adaptador.
     * @param viewType El tipo de vista de la nueva Vista.
     * @return Un nuevo ViewHolder que contiene una Vista del tipo de vista dado.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Reusamos el diseño 'item_movie.xml' que ya creaste
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return ViewHolder(view)
    }

    /**
     * Se llama por RecyclerView para mostrar los datos en la posición especificada.
     * @param holder El ViewHolder que debe actualizarse para representar el contenido del elemento en la posición dada en el conjunto de datos.
     * @param position La posición del elemento dentro del conjunto de datos del adaptador.
     */
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

    /**
     * Establece una nueva lista de películas en el adaptador, borrando las anteriores.
     * @param newList La nueva lista de películas locales.
     */
    fun setList(newList: List<PeliculaLocal>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * Agrega una lista de películas al final de la lista actual.
     * @param newItems La lista de nuevas películas locales a agregar.
     */
    fun addMovies(newItems: List<PeliculaLocal>) {
        if (newItems.isEmpty()) return
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    /**
     * Obtiene la lista actual de películas en el adaptador.
     * @return La lista de películas locales.
     */
    fun getItems(): List<PeliculaLocal> = items

    /**
     * Devuelve el número total de elementos en el conjunto de datos que tiene el adaptador.
     * @return El número total de elementos en este adaptador.
     */
    override fun getItemCount() = items.size
}
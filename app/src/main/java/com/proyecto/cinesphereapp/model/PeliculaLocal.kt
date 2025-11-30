package com.proyecto.cinesphereapp.model

/**
 * Representa una película tal como se almacena en la base de datos local.
 * @property id El ID de la película (de TMDB).
 * @property titulo El título de la película.
 * @property posterPath La ruta del póster de la película.
 * @property estado El estado de visualización de la película (e.g., "PENDIENTE", "VISTO").
 */
data class PeliculaLocal(
    val id: Int,
    val titulo: String,
    val posterPath: String?,
    val estado: String
)
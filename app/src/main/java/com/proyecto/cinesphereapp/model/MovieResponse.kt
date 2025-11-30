package com.proyecto.cinesphereapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa la respuesta de la API de TMDB para una lista de películas.
 * @property page El número de la página actual.
 * @property totalPages El número total de páginas disponibles.
 * @property results La lista de películas.
 */
data class MovieResponse(
    @SerializedName("page") val page: Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    @SerializedName("results") val results: List<MovieDto>
)

/**
 * Representa un objeto de transferencia de datos (DTO) para una película individual.
 * @property id El ID de la película.
 * @property title El título de la película.
 * @property posterPath La ruta del póster de la película.
 * @property overview La sinopsis de la película.
 * @property rating La calificación promedio de la película.
 * @property releaseDate La fecha de lanzamiento de la película.
 */
data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val rating: Double,
    @SerializedName("release_date") val releaseDate: String?
)
package com.proyecto.cinesphereapp.model

import com.google.gson.annotations.SerializedName

// La respuesta principal que envuelve la lista
data class MovieResponse(
    @SerializedName("page") val page: Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    @SerializedName("results") val results: List<MovieDto>
)

// El objeto de cada película individual
data class MovieDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("overview") val overview: String,
    @SerializedName("vote_average") val rating: Double,
    @SerializedName("release_date") val releaseDate: String?
)
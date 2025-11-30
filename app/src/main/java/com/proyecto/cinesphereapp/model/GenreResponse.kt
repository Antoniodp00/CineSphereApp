package com.proyecto.cinesphereapp.model

import com.google.gson.annotations.SerializedName

/**
 * Representa la respuesta de la API de géneros de TMDB.
 * @property genres La lista de géneros devuelta por la API.
 */
data class GenreResponse(
    @SerializedName("genres") val genres: List<GenreDto>
)

/**
 * Representa un único género de película.
 * @property id El ID único del género.
 * @property name El nombre del género.
 */
data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) {
    /**
     * Devuelve el nombre del género.
     * Esta sobrescritura es útil para mostrar el nombre del género en componentes de la interfaz de usuario como un Spinner.
     * @return El nombre del género.
     */
    override fun toString(): String {
        return name
    }
}
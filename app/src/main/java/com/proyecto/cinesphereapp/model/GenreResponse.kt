package com.proyecto.cinesphereapp.model

import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("genres") val genres: List<GenreDto>
)

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) {
    // Sobrescribimos toString para que el Spinner muestre el nombre, no el objeto
    override fun toString(): String {
        return name
    }
}
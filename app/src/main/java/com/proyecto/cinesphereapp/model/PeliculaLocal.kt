package com.proyecto.cinesphereapp.model

data class PeliculaLocal(
    val id: Int,
    val titulo: String,
    val posterPath: String?,
    val estado: String // "PENDIENTE", "VISTO", etc.
)
package com.proyecto.cinesphereapp.model

import android.provider.BaseColumns

object CineSphereContract {
    // Tabla Usuarios
    object UsuarioEntry : BaseColumns {
        const val TABLE_NAME = "usuarios"
        const val COLUMN_NOMBRE = "nombre_usuario"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    // Tabla Mi Lista (Películas guardadas)
    object MiListaEntry : BaseColumns {
        const val TABLE_NAME = "mi_lista"
        const val COLUMN_USER_ID = "usuario_id"
        const val COLUMN_MOVIE_ID = "pelicula_id" // ID de TMDB
        const val COLUMN_TITLE = "titulo"
        const val COLUMN_POSTER = "poster_url"
        const val COLUMN_ESTADO = "estado" // PENDIENTE, VISTO...
    }
}
package com.proyecto.cinesphereapp.model

import android.provider.BaseColumns

/**
 * Define el esquema de la base de datos (contrato) para la aplicación CineSphere.
 * Contiene constantes para los nombres de las tablas y columnas, asegurando consistencia
 * y facilitando el mantenimiento del código de la base de datos.
 */
object CineSphereContract {
    /**
     * Define el contenido de la tabla `usuarios`.
     * Cada entrada en esta tabla representa un usuario registrado en la aplicación.
     */
    object UsuarioEntry : BaseColumns {
        const val TABLE_NAME = "usuarios"
        const val COLUMN_NOMBRE = "nombre_usuario"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    /**
     * Define el contenido de la tabla `mi_lista`.
     * Cada entrada representa una película que un usuario ha guardado en su lista personal.
     */
    object MiListaEntry : BaseColumns {
        const val TABLE_NAME = "mi_lista"
        const val COLUMN_USER_ID = "usuario_id"
        const val COLUMN_MOVIE_ID = "pelicula_id" // ID de TMDB
        const val COLUMN_TITLE = "titulo"
        const val COLUMN_POSTER = "poster_url"
        const val COLUMN_ESTADO = "estado" // PENDIENTE, VISTO...
    }
}
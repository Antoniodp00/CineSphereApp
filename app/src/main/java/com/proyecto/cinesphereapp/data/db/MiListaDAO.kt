package com.proyecto.cinesphereapp.data.db

import android.content.ContentValues
import android.content.Context
import com.proyecto.cinesphereapp.model.CineSphereContract
import com.proyecto.cinesphereapp.model.CineSphereContract.MiListaEntry
import com.proyecto.cinesphereapp.model.PeliculaLocal
import android.provider.BaseColumns

/**
 * DAO (Data Access Object) para interactuar con la tabla `mi_lista`.
 * Proporciona métodos para agregar, eliminar, obtener y actualizar películas en la lista de un usuario.
 * @param context El contexto de la aplicación.
 */
class MiListaDao(context: Context) {
    private val dbHelper = CineSphereDbHelper(context)

    /**
     * Guarda una película en la lista del usuario.
     * @param userId El ID del usuario.
     * @param movieId El ID de la película.
     * @param titulo El título de la película.
     * @param poster La ruta del póster de la película.
     * @param estado El estado inicial de la película (por defecto "PENDIENTE").
     * @return el ID de la fila insertada, o -1 si ya existe.
     */
    fun agregarPelicula(userId: Int, movieId: Int, titulo: String, poster: String, estado: String = "PENDIENTE"): Long {
        // 1. Evitar duplicados
        if (existePelicula(userId, movieId)) {
            return -1
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(MiListaEntry.COLUMN_USER_ID, userId)
            put(MiListaEntry.COLUMN_MOVIE_ID, movieId)
            put(MiListaEntry.COLUMN_TITLE, titulo)
            put(MiListaEntry.COLUMN_POSTER, poster)
            put(MiListaEntry.COLUMN_ESTADO, estado)
        }

        return db.insert(MiListaEntry.TABLE_NAME, null, values)
    }

    /**
     * Elimina una película de la lista de un usuario.
     * @param userId El ID del usuario.
     * @param movieId El ID de la película.
     * @return El número de filas eliminadas.
     */
    fun eliminarPelicula(userId: Int, movieId: Int): Int {
        val db = dbHelper.writableDatabase
        val whereClause = "${MiListaEntry.COLUMN_USER_ID} = ? AND ${MiListaEntry.COLUMN_MOVIE_ID} = ?"
        val whereArgs = arrayOf(userId.toString(), movieId.toString())

        return db.delete(MiListaEntry.TABLE_NAME, whereClause, whereArgs)
    }

    /**
     * Obtiene la lista completa de películas guardadas por un usuario (sin paginar).
     * @param userId El ID del usuario.
     * @return Una lista de objetos [PeliculaLocal].
     */
    fun obtenerListaUsuario(userId: Int): List<PeliculaLocal> {
        val db = dbHelper.readableDatabase
        val lista = ArrayList<PeliculaLocal>()

        val selection = "${MiListaEntry.COLUMN_USER_ID} = ?"
        val selectionArgs = arrayOf(userId.toString())

        val cursor = db.query(
            MiListaEntry.TABLE_NAME,
            null, // Todas las columnas
            selection,
            selectionArgs,
            null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(MiListaEntry.COLUMN_MOVIE_ID))
                val titulo = getString(getColumnIndexOrThrow(MiListaEntry.COLUMN_TITLE))
                val poster = getString(getColumnIndexOrThrow(MiListaEntry.COLUMN_POSTER))
                val estado = getString(getColumnIndexOrThrow(MiListaEntry.COLUMN_ESTADO))

                lista.add(PeliculaLocal(id, titulo, poster, estado))
            }
            close()
        }
        return lista
    }

    /**
     * Obtiene una página de películas guardadas por un usuario, ordenadas por _id DESC.
     * @param userId El ID del usuario.
     * @param limit El número máximo de películas a devolver.
     * @param offset El número de películas a saltar.
     * @return Una lista paginada de objetos [PeliculaLocal].
     */
    fun obtenerListaUsuarioPaginado(userId: Int, limit: Int, offset: Int): List<PeliculaLocal> {
        val db = dbHelper.readableDatabase
        val lista = ArrayList<PeliculaLocal>()
        val sql = "SELECT ${MiListaEntry.COLUMN_MOVIE_ID}, ${MiListaEntry.COLUMN_TITLE}, ${MiListaEntry.COLUMN_POSTER}, ${MiListaEntry.COLUMN_ESTADO} " +
                "FROM ${MiListaEntry.TABLE_NAME} WHERE ${MiListaEntry.COLUMN_USER_ID} = ? " +
                "ORDER BY ${BaseColumns._ID} DESC LIMIT ? OFFSET ?"
        val cursor = db.rawQuery(sql, arrayOf(userId.toString(), limit.toString(), offset.toString()))
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(0)
                val titulo = getString(1)
                val poster = getString(2)
                val estado = getString(3)
                lista.add(PeliculaLocal(id, titulo, poster, estado))
            }
            close()
        }
        return lista
    }

    /**
     * Verifica si una película ya está guardada por ese usuario.
     * @param userId El ID del usuario.
     * @param movieId El ID de la película.
     * @return `true` si la película existe, `false` en caso contrario.
     */
    fun existePelicula(userId: Int, movieId: Int): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM ${MiListaEntry.TABLE_NAME} WHERE ${MiListaEntry.COLUMN_USER_ID} = ? AND ${MiListaEntry.COLUMN_MOVIE_ID} = ?",
            arrayOf(userId.toString(), movieId.toString())
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    /**
     * Cuenta el número total de películas en la lista de un usuario.
     * @param userId El ID del usuario.
     * @return El número total de películas.
     */
    fun contarPeliculas(userId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${MiListaEntry.TABLE_NAME} WHERE ${MiListaEntry.COLUMN_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    /**
     * Cuenta el número de películas en la lista de un usuario con un estado específico.
     * @param userId El ID del usuario.
     * @param estado El estado de la película a contar.
     * @return El número de películas con el estado especificado.
     */
    fun contarPorEstado(userId: Int, estado: String): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${MiListaEntry.TABLE_NAME} WHERE ${MiListaEntry.COLUMN_USER_ID} = ? AND ${MiListaEntry.COLUMN_ESTADO} = ?",
            arrayOf(userId.toString(), estado)
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    /**
     * Actualiza el estado de una película (ej: de "PENDIENTE" a "VISTO").
     * @param userId El ID del usuario.
     * @param movieId El ID de la película.
     * @param nuevoEstado El nuevo estado de la película.
     */
    fun actualizarEstado(userId: Int, movieId: Int, nuevoEstado: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CineSphereContract.MiListaEntry.COLUMN_ESTADO, nuevoEstado)
        }

        val whereClause = "${CineSphereContract.MiListaEntry.COLUMN_USER_ID} = ? AND ${CineSphereContract.MiListaEntry.COLUMN_MOVIE_ID} = ?"
        val whereArgs = arrayOf(userId.toString(), movieId.toString())

        db.update(CineSphereContract.MiListaEntry.TABLE_NAME, values, whereClause, whereArgs)
    }
}
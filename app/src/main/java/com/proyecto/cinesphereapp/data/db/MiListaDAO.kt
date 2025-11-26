package com.proyecto.cinesphereapp.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.proyecto.cinesphereapp.model.CineSphereContract.MiListaEntry
import com.proyecto.cinesphereapp.model.PeliculaLocal

class MiListaDao(context: Context) {
    private val dbHelper = CineSphereDbHelper(context)

    /**
     * Guarda una película en la lista del usuario.
     * Si ya existe, no hace nada (o podrías actualizar el estado).
     */
    fun agregarPelicula(userId: Int, movieId: Int, titulo: String, poster: String, estado: String = "PENDIENTE"): Long {
        // Primero verificamos si ya existe para evitar duplicados
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
     * Elimina una película de la lista del usuario.
     */
    fun eliminarPelicula(userId: Int, movieId: Int): Int {
        val db = dbHelper.writableDatabase
        val whereClause = "${MiListaEntry.COLUMN_USER_ID} = ? AND ${MiListaEntry.COLUMN_MOVIE_ID} = ?"
        val whereArgs = arrayOf(userId.toString(), movieId.toString())

        return db.delete(MiListaEntry.TABLE_NAME, whereClause, whereArgs)
    }

    /**
     * Obtiene todas las películas guardadas por un usuario específico.
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
                val p = PeliculaLocal(
                    id = getInt(getColumnIndexOrThrow(MiListaEntry.COLUMN_MOVIE_ID)),
                    titulo = getString(getColumnIndex
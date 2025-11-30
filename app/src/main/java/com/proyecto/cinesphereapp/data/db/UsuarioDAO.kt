package com.proyecto.cinesphereapp.data.db

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.proyecto.cinesphereapp.model.CineSphereContract.UsuarioEntry

/**
 * DAO (Data Access Object) para la entidad Usuario.
 * Proporciona métodos para registrar y autenticar usuarios en la base de datos.
 * @param context El contexto de la aplicación.
 */
class UsuarioDao(context: Context) {
    private val dbHelper = CineSphereDbHelper(context)

    /**
     * Registra un nuevo usuario en la base de datos.
     * @param nombre El nombre de usuario.
     * @param email El correo electrónico del usuario.
     * @param pass La contraseña del usuario.
     * @return El ID de la nueva fila insertada, o -1 si hubo un error (por ejemplo, si el nombre de usuario ya existe debido a una restricción UNIQUE).
     */
    fun registrar(nombre: String, email: String, pass: String): Long {
        // Obtenemos la base de datos en modo escritura
        val db = dbHelper.writableDatabase

        // Preparamos los valores (Columna -> Valor)
        val values = ContentValues().apply {
            put(UsuarioEntry.COLUMN_NOMBRE, nombre)
            put(UsuarioEntry.COLUMN_EMAIL, email)
            put(UsuarioEntry.COLUMN_PASSWORD, pass) // TODO: En una app real, aquí hashearíamos la contraseña
        }

        // Insertamos. Devuelve el ID de la nueva fila o -1 si hubo error
        return db.insert(UsuarioEntry.TABLE_NAME, null, values)
    }

    /**
     * Valida las credenciales de un usuario.
     * @param nombre El nombre de usuario a autenticar.
     * @param pass La contraseña a verificar.
     * @return El ID del usuario si las credenciales son correctas, de lo contrario, null.
     */
    fun login(nombre: String, pass: String): Int? {
        val db = dbHelper.readableDatabase

        // Columnas que queremos recuperar
        val projection = arrayOf(BaseColumns._ID, UsuarioEntry.COLUMN_PASSWORD)

        // Condición WHERE: nombre = ?
        val selection = "${UsuarioEntry.COLUMN_NOMBRE} = ?"
        val selectionArgs = arrayOf(nombre)

        val cursor = db.query(
            UsuarioEntry.TABLE_NAME, // Tabla
            projection,              // Columnas
            selection,               // Where
            selectionArgs,           // Valores del Where
            null, null, null
        )

        var userId: Int? = null

        with(cursor) {
            if (moveToFirst()) {
                // Recuperamos la contraseña de la BD
                val dbPass = getString(getColumnIndexOrThrow(UsuarioEntry.COLUMN_PASSWORD))

                // Comparamos con la que introdujo el usuario
                if (dbPass == pass) {
                    userId = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                }
            }
            close() // ¡Importante cerrar el cursor!
        }
        return userId
    }
}
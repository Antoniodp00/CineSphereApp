package com.proyecto.cinesphereapp.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.proyecto.cinesphereapp.model.CineSphereContract.MiListaEntry
import com.proyecto.cinesphereapp.model.CineSphereContract.UsuarioEntry
import android.provider.BaseColumns
import com.proyecto.cinesphereapp.model.CineSphereContract

class CineSphereDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "CineSphere.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // SQL para crear tabla Usuarios
        val SQL_CREATE_USUARIOS = """
            CREATE TABLE ${CineSphereContract.UsuarioEntry.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${UsuarioEntry.COLUMN_NOMBRE} TEXT NOT NULL UNIQUE,
                ${UsuarioEntry.COLUMN_EMAIL} TEXT,
                ${UsuarioEntry.COLUMN_PASSWORD} TEXT NOT NULL
            );
        """.trimIndent()

        // SQL para crear tabla Mi Lista
        val SQL_CREATE_MI_LISTA = """
            CREATE TABLE ${MiListaEntry.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${MiListaEntry.COLUMN_USER_ID} INTEGER,
                ${MiListaEntry.COLUMN_MOVIE_ID} INTEGER,
                ${MiListaEntry.COLUMN_TITLE} TEXT,
                ${MiListaEntry.COLUMN_POSTER} TEXT,
                ${MiListaEntry.COLUMN_ESTADO} TEXT,
                FOREIGN KEY(${MiListaEntry.COLUMN_USER_ID}) REFERENCES ${UsuarioEntry.TABLE_NAME}(${BaseColumns._ID})
            );
        """.trimIndent()

        db.execSQL(SQL_CREATE_USUARIOS)
        db.execSQL(SQL_CREATE_MI_LISTA)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Borrar y crear de nuevo si cambia la versión (para desarrollo)
        db.execSQL("DROP TABLE IF EXISTS ${UsuarioEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${MiListaEntry.TABLE_NAME}")
        onCreate(db)
    }

    // Activar claves foráneas al abrir la conexión (Importante para SQLite)
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}
package com.proyecto.cinesphereapp.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.MiListaDao

class DetailActivity : AppCompatActivity() {

    private lateinit var btnAddList: Button
    private lateinit var dao: MiListaDao

    // Datos de la película
    private var movieId: Int = 0
    private var movieTitle: String = ""
    private var moviePoster: String = ""
    private var userId: Int = -1
    private var isAdded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // 1. Inicializar DAO y Usuario
        dao = MiListaDao(this)
        val prefs = getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        userId = prefs.getInt("USER_ID", -1)

        // 2. Recibir datos del Intent
        movieId = intent.getIntExtra("EXTRA_ID", 0)
        movieTitle = intent.getStringExtra("EXTRA_TITLE") ?: ""
        moviePoster = intent.getStringExtra("EXTRA_POSTER") ?: ""
        val movieOverview = intent.getStringExtra("EXTRA_OVERVIEW")
        val movieRating = intent.getDoubleExtra("EXTRA_RATING", 0.0)
        val movieDate = intent.getStringExtra("EXTRA_DATE")

        // 3. Configurar UI
        val ivPoster = findViewById<ImageView>(R.id.ivDetailPoster)
        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvRating = findViewById<TextView>(R.id.tvDetailRating)
        val tvDate = findViewById<TextView>(R.id.tvDetailDate)
        val tvOverview = findViewById<TextView>(R.id.tvDetailOverview)
        btnAddList = findViewById(R.id.btnAddList)

        tvTitle.text = movieTitle
        tvRating.text = "★ $movieRating"
        tvDate.text = movieDate ?: "Fecha desconocida"
        tvOverview.text = movieOverview ?: "Sin descripción disponible."

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$moviePoster")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(ivPoster)

        // 4. Comprobar estado inicial (¿Está en mi lista?)
        checkIfMovieIsAdded()

        // 5. Acción del botón
        btnAddList.setOnClickListener {
            if (isAdded) {
                eliminarDeLista()
            } else {
                anadirALista()
            }
        }
    }

    private fun checkIfMovieIsAdded() {
        // Hacemos la consulta en un hilo secundario para no bloquear la UI (buena práctica)
        Thread {
            val exists = dao.existePelicula(userId, movieId)
            runOnUiThread {
                isAdded = exists
                updateButtonUI()
            }
        }.start()
    }

    private fun updateButtonUI() {
        if (isAdded) {
            btnAddList.text = "Eliminar de Mi Lista"
            btnAddList.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
        } else {
            btnAddList.text = "Añadir a Mi Lista"
            btnAddList.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_blue_light) // O tu color #3fa9f5
        }
    }

    private fun anadirALista() {
        Thread {
            // Guardamos en SQLite
            val result = dao.agregarPelicula(userId, movieId, movieTitle, moviePoster)
            runOnUiThread {
                if (result != -1L) {
                    Toast.makeText(this, "Guardada en favoritos", Toast.LENGTH_SHORT).show()
                    isAdded = true
                    updateButtonUI()
                } else {
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun eliminarDeLista() {
        Thread {
            val rows = dao.eliminarPelicula(userId, movieId)
            runOnUiThread {
                if (rows > 0) {
                    Toast.makeText(this, "Eliminada de favoritos", Toast.LENGTH_SHORT).show()
                    isAdded = false
                    updateButtonUI()
                }
            }
        }.start()
    }
}
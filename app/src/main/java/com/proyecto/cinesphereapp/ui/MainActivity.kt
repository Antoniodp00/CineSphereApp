package com.proyecto.cinesphereapp

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.proyecto.cinesphereapp.data.network.RetrofitClient
import com.proyecto.cinesphereapp.model.GenreDto
import com.proyecto.cinesphereapp.ui.MovieAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var rvMovies: RecyclerView
    private lateinit var adapter: MovieAdapter
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var fabFilter: FloatingActionButton

    // Data & State
    private val API_KEY = "377436a43ac5d7e3db5d9d058102d17b"
    private var genreList: List<GenreDto> = emptyList()

    // Variables para RECORDAR la selección del usuario en los filtros
    private var selectedYearPosition: Int = 0
    private var selectedRatingPosition: Int = 0
    private var selectedGenrePosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()

        // Carga inicial
        loadMovies(TipoCarga.POPULARES)
        loadGenres() // Cargar géneros en segundo plano para tenerlos listos

        // Eventos Principales
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            if (query.isNotEmpty()) loadMovies(TipoCarga.BUSQUEDA, query = query)
        }

        // Evento del Botón Flotante -> Abrir el Sheet
        fabFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun initViews() {
        rvMovies = findViewById(R.id.rvMovies)
        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        fabFilter = findViewById(R.id.fabFilter)
    }

    // --- LÓGICA DEL BOTTOM SHEET ---
    private fun showFilterBottomSheet() {
        // 1. Inflar el diseño del BottomSheet
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.layout_filter_bottom_sheet, null)
        dialog.setContentView(view)

        // 2. Obtener referencias a los componentes DENTRO del Sheet
        val spYear = view.findViewById<Spinner>(R.id.spSheetYear)
        val spRating = view.findViewById<Spinner>(R.id.spSheetRating)
        val spGenre = view.findViewById<Spinner>(R.id.spSheetGenre)
        val btnApply = view.findViewById<Button>(R.id.btnSheetApply)
        val btnClear = view.findViewById<Button>(R.id.btnSheetClear)

        // 3. Configurar adaptadores (igual que hacíamos antes, pero ahora dentro del dialog)
        val years = mutableListOf("Año")
        years.addAll((2024 downTo 1950).map { it.toString() })
        spYear.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, years)

        val ratings = listOf("Rating", "9", "8", "7", "6", "5", "4", "3", "2", "1")
        spRating.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ratings)

        val displayGenres = mutableListOf<Any>("Género")
        displayGenres.addAll(genreList)
        spGenre.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, displayGenres)

        // 4. Restaurar selección previa (para que recuerde lo que elegiste)
        spYear.setSelection(selectedYearPosition)
        spRating.setSelection(selectedRatingPosition)
        spGenre.setSelection(selectedGenrePosition)

        // 5. Evento Aplicar
        btnApply.setOnClickListener {
            // Guardamos la selección actual para la próxima vez
            selectedYearPosition = spYear.selectedItemPosition
            selectedRatingPosition = spRating.selectedItemPosition
            selectedGenrePosition = spGenre.selectedItemPosition

            // Obtenemos valores reales
            val yearParam = if (spYear.selectedItemPosition > 0) spYear.selectedItem.toString() else null
            val ratingParam = if (spRating.selectedItemPosition > 0) spRating.selectedItem.toString() else null
            val genreObj = spGenre.selectedItem
            val genreParam = if (genreObj is GenreDto) genreObj.id.toString() else null

            // Llamamos a la API
            loadMovies(TipoCarga.FILTRO, year = yearParam, rating = ratingParam, genre = genreParam)

            // Cerramos el menú
            dialog.dismiss()
        }

        // 6. Evento Limpiar
        btnClear.setOnClickListener {
            selectedYearPosition = 0
            selectedRatingPosition = 0
            selectedGenrePosition = 0

            etSearch.text.clear()
            loadMovies(TipoCarga.POPULARES)
            dialog.dismiss()
        }

        dialog.show()
    }

    // ... (setupRecyclerView, loadGenres, loadMovies se quedan igual que en la respuesta anterior) ...
    // Solo recuerda quitar el método "setupSpinners" antiguo ya que ahora se hace dentro de showFilterBottomSheet

    private fun setupRecyclerView() {
        rvMovies.layoutManager = GridLayoutManager(this, 2)
        adapter = MovieAdapter(emptyList()) { movie ->
            Toast.makeText(this, "Seleccionada: ${movie.title}", Toast.LENGTH_SHORT).show()
            // TODO: Ir a detalles
        }
        rvMovies.adapter = adapter
    }

    private fun loadGenres() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getGenres(API_KEY)
                genreList = response.genres
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    enum class TipoCarga { POPULARES, BUSQUEDA, FILTRO }

    private fun loadMovies(tipo: TipoCarga, query: String = "", year: String? = null, rating: String? = null, genre: String? = null) {
        lifecycleScope.launch {
            try {
                val response = when (tipo) {
                    TipoCarga.POPULARES -> RetrofitClient.instance.getPopularMovies(API_KEY)
                    TipoCarga.BUSQUEDA -> RetrofitClient.instance.searchMovies(API_KEY, query)
                    TipoCarga.FILTRO -> RetrofitClient.instance.discoverMovies(
                        apiKey = API_KEY,
                        year = year,
                        minRating = rating,
                        genreId = genre
                    )
                }
                adapter.updateMovies(response.results)

                if (response.results.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No se encontraron películas", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
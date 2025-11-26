package com.proyecto.cinesphereapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.network.RetrofitClient
import com.proyecto.cinesphereapp.model.GenreDto
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout específico del fragmento
        return inflater.inflate(R.layout.activity_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()

        // Carga inicial
        loadMovies(TipoCarga.POPULARES)
        loadGenres() // Cargar géneros en segundo plano

        // Eventos Principales
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            if (query.isNotEmpty()) {
                loadMovies(TipoCarga.BUSQUEDA, query = query)
            } else {
                loadMovies(TipoCarga.POPULARES)
            }
        }

        // Evento del Botón Flotante -> Abrir el Sheet
        fabFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun initViews(view: View) {
        rvMovies = view.findViewById(R.id.rvMovies)
        etSearch = view.findViewById(R.id.etSearch)
        btnSearch = view.findViewById(R.id.btnSearch)
        fabFilter = view.findViewById(R.id.fabFilter)
    }

    private fun setupRecyclerView() {
        rvMovies.layoutManager = GridLayoutManager(requireContext(), 2)

        // Modificamos la lambda del click:
        adapter = MovieAdapter(emptyList()) { movie ->
            val intent = Intent(requireContext(), DetailActivity::class.java)

            // Pasamos los datos "desglosados" a través del Intent
            intent.putExtra("EXTRA_ID", movie.id)
            intent.putExtra("EXTRA_TITLE", movie.title)
            intent.putExtra("EXTRA_POSTER", movie.posterPath)
            intent.putExtra("EXTRA_OVERVIEW", movie.overview)
            intent.putExtra("EXTRA_RATING", movie.rating)
            intent.putExtra("EXTRA_DATE", movie.releaseDate)

            startActivity(intent)
        }
        rvMovies.adapter = adapter
    }

    // --- LÓGICA DEL BOTTOM SHEET (Filtros) ---
    private fun showFilterBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_filter_bottom_sheet, null)
        dialog.setContentView(view)

        val spYear = view.findViewById<Spinner>(R.id.spSheetYear)
        val spRating = view.findViewById<Spinner>(R.id.spSheetRating)
        val spGenre = view.findViewById<Spinner>(R.id.spSheetGenre)
        val btnApply = view.findViewById<Button>(R.id.btnSheetApply)
        val btnClear = view.findViewById<Button>(R.id.btnSheetClear)

        // Configurar adaptadores
        val years = mutableListOf("Año")
        years.addAll((2024 downTo 1950).map { it.toString() })
        spYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        val ratings = listOf("Rating", "9", "8", "7", "6", "5", "4", "3", "2", "1")
        spRating.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, ratings)

        val displayGenres = mutableListOf<Any>("Género")
        displayGenres.addAll(genreList)
        spGenre.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, displayGenres)

        // Restaurar selección previa
        spYear.setSelection(selectedYearPosition)
        spRating.setSelection(selectedRatingPosition)
        spGenre.setSelection(selectedGenrePosition)

        // Evento Aplicar
        btnApply.setOnClickListener {
            selectedYearPosition = spYear.selectedItemPosition
            selectedRatingPosition = spRating.selectedItemPosition
            selectedGenrePosition = spGenre.selectedItemPosition

            val yearParam = if (spYear.selectedItemPosition > 0) spYear.selectedItem.toString() else null
            val ratingParam = if (spRating.selectedItemPosition > 0) spRating.selectedItem.toString() else null
            val genreObj = spGenre.selectedItem
            val genreParam = if (genreObj is GenreDto) genreObj.id.toString() else null

            loadMovies(TipoCarga.FILTRO, year = yearParam, rating = ratingParam, genre = genreParam)
            dialog.dismiss()
        }

        // Evento Limpiar
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
                    Toast.makeText(requireContext(), "No se encontraron películas", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
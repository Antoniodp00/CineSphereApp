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

    // Paginación
    private var isLoading = false
    private var isLastPage = false
    private var currentPage = 1
    private var totalPages: Int? = null

    // Contexto de la última carga para soportar scroll infinito con búsqueda/filtros
    private var lastTipo: TipoCarga = TipoCarga.POPULARES
    private var lastQuery: String = ""
    private var lastYear: String? = null
    private var lastRating: String? = null
    private var lastGenre: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()

        // Carga inicial
        resetAndLoad(TipoCarga.POPULARES)
        loadGenres() // Cargar géneros en segundo plano

        // Eventos Principales
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString()
            if (query.isNotEmpty()) {
                resetAndLoad(TipoCarga.BUSQUEDA, query = query)
            } else {
                resetAndLoad(TipoCarga.POPULARES)
            }
        }

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
        val layoutManager = GridLayoutManager(requireContext(), 2)
        rvMovies.layoutManager = layoutManager

        adapter = MovieAdapter(emptyList()) { movie ->
            val fragment = DetailFragment.newInstance(
                id = movie.id,
                title = movie.title,
                poster = movie.posterPath,
                overview = movie.overview,
                rating = movie.rating,
                date = movie.releaseDate
            )
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }
        rvMovies.adapter = adapter

        rvMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val threshold = 6 // carga cuando queden ~6 items
                if (!isLoading && !isLastPage &&
                    (visibleItemCount + firstVisibleItemPosition + threshold) >= totalItemCount
                ) {
                    loadNextPage()
                }
            }
        })
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

        val years = mutableListOf("Año")
        years.addAll((2024 downTo 1950).map { it.toString() })
        spYear.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        val ratings = listOf("Rating", "9", "8", "7", "6", "5", "4", "3", "2", "1")
        spRating.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, ratings)

        val displayGenres = mutableListOf<Any>("Género")
        displayGenres.addAll(genreList)
        spGenre.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, displayGenres)

        spYear.setSelection(selectedYearPosition)
        spRating.setSelection(selectedRatingPosition)
        spGenre.setSelection(selectedGenrePosition)

        btnApply.setOnClickListener {
            selectedYearPosition = spYear.selectedItemPosition
            selectedRatingPosition = spRating.selectedItemPosition
            selectedGenrePosition = spGenre.selectedItemPosition

            val yearParam = if (spYear.selectedItemPosition > 0) spYear.selectedItem.toString() else null
            val ratingParam = if (spRating.selectedItemPosition > 0) spRating.selectedItem.toString() else null
            val genreObj = spGenre.selectedItem
            val genreParam = if (genreObj is GenreDto) genreObj.id.toString() else null

            resetAndLoad(TipoCarga.FILTRO, year = yearParam, rating = ratingParam, genre = genreParam)
            dialog.dismiss()
        }

        btnClear.setOnClickListener {
            selectedYearPosition = 0
            selectedRatingPosition = 0
            selectedGenrePosition = 0

            etSearch.text.clear()
            resetAndLoad(TipoCarga.POPULARES)
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

    private fun resetAndLoad(tipo: TipoCarga, query: String = "", year: String? = null, rating: String? = null, genre: String? = null) {
        lastTipo = tipo
        lastQuery = query
        lastYear = year
        lastRating = rating
        lastGenre = genre
        isLoading = false
        isLastPage = false
        currentPage = 1
        totalPages = null
        adapter.setMovies(emptyList())
        loadMoviesPage(currentPage)
    }

    private fun loadNextPage() {
        if (isLoading || isLastPage) return
        val next = currentPage + 1
        loadMoviesPage(next)
    }

    private fun loadMoviesPage(page: Int) {
        isLoading = true
        lifecycleScope.launch {
            try {
                val response = when (lastTipo) {
                    TipoCarga.POPULARES -> RetrofitClient.instance.getPopularMovies(API_KEY, page = page)
                    TipoCarga.BUSQUEDA -> RetrofitClient.instance.searchMovies(API_KEY, lastQuery, page = page)
                    TipoCarga.FILTRO -> RetrofitClient.instance.discoverMovies(
                        apiKey = API_KEY,
                        year = lastYear,
                        minRating = lastRating,
                        genreId = lastGenre,
                        page = page
                    )
                }

                if (page == 1) {
                    adapter.setMovies(response.results)
                } else {
                    adapter.addMovies(response.results)
                }

                currentPage = page
                totalPages = response.totalPages ?: totalPages
                isLastPage = totalPages?.let { currentPage >= it } ?: (response.results.isEmpty())

                if (response.results.isEmpty() && page == 1) {
                    Toast.makeText(requireContext(), "No se encontraron películas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }
}
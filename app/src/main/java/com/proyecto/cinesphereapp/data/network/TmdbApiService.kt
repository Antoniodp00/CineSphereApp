package com.proyecto.cinesphereapp.data.network

import com.proyecto.cinesphereapp.model.GenreResponse
import com.proyecto.cinesphereapp.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApiService {

    // ... (Tus métodos existentes getPopularMovies y searchMovies se quedan igual) ...
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String): MovieResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): MovieResponse

    // --- NUEVOS MÉTODOS PARA FILTROS ---

    // 1. Obtener lista de géneros para el desplegable
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): GenreResponse

    // 2. Filtrado avanzado (Discover)
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("primary_release_year") year: String?,
        @Query("vote_average.gte") minRating: String?, // Greater than or equal
        @Query("with_genres") genreId: String?
    ): MovieResponse
}
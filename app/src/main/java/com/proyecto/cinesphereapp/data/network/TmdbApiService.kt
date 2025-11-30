package com.proyecto.cinesphereapp.data.network

import com.proyecto.cinesphereapp.model.GenreResponse
import com.proyecto.cinesphereapp.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz que define los endpoints de la API de TMDB utilizando Retrofit.
 */
interface TmdbApiService {

    /**
     * Obtiene una lista de las películas más populares.
     * @param apiKey La clave de la API de TMDB.
     * @param page El número de página a solicitar.
     * @param language El idioma de la respuesta.
     * @return Un objeto [MovieResponse] que contiene una lista de películas.
     */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieResponse

    /**
     * Busca películas que coincidan con una consulta de texto.
     * @param apiKey La clave de la API de TMDB.
     * @param query El texto a buscar.
     * @param page El número de página a solicitar.
     * @param language El idioma de la respuesta.
     * @return Un objeto [MovieResponse] que contiene una lista de películas coincidentes.
     */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("language") language: String = "es-ES"
    ): MovieResponse

    /**
     * Obtiene la lista oficial de géneros de películas de TMDB.
     * @param apiKey La clave de la API de TMDB.
     * @param language El idioma de la respuesta.
     * @return Un objeto [GenreResponse] que contiene una lista de géneros.
     */
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES"
    ): GenreResponse

    /**
     * Descubre películas según varios filtros, como año, calificación y género.
     * @param apiKey La clave de la API de TMDB.
     * @param language El idioma de la respuesta.
     * @param year El año de lanzamiento principal.
     * @param minRating La calificación mínima (mayor o igual que).
     * @param genreId El ID del género a incluir.
     * @param page El número de página a solicitar.
     * @return Un objeto [MovieResponse] que contiene una lista de películas filtradas.
     */
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "es-ES",
        @Query("primary_release_year") year: String?,
        @Query("vote_average.gte") minRating: String?, // Greater than or equal
        @Query("with_genres") genreId: String?,
        @Query("page") page: Int = 1
    ): MovieResponse
}
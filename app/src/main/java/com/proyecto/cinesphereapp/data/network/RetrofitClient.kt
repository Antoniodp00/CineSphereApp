package com.proyecto.cinesphereapp.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton para configurar y proporcionar una instancia de Retrofit.
 * Se utiliza para realizar llamadas a la API de TMDB.
 */
object RetrofitClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    /**
     * Instancia de [TmdbApiService] creada de forma perezosa (lazy).
     * La instancia de Retrofit se configura con la URL base y un convertidor Gson.
     */
    val instance: TmdbApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbApiService::class.java)
    }
}
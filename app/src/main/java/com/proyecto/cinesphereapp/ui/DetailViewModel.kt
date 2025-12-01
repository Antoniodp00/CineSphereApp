package com.proyecto.cinesphereapp.ui

import androidx.lifecycle.ViewModel

/**
 * ViewModel para DetailFragment. Almacena los detalles de la película y el estado de la interfaz de usuario.
 */
class DetailViewModel : ViewModel() {
    var initialized: Boolean = false
    var movieId: Int = 0
    var movieTitle: String = ""
    var moviePoster: String = ""
    var movieOverview: String? = null
    var movieRating: Double = 0.0
    var movieDate: String? = null

    var isAdded: Boolean = false
    var knownAddedState: Boolean = false
}

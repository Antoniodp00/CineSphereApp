package com.proyecto.cinesphereapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.MiListaDao

/**
 * Fragmento que muestra los detalles de una película.
 */
class DetailFragment : Fragment() {

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_POSTER = "arg_poster"
        private const val ARG_OVERVIEW = "arg_overview"
        private const val ARG_RATING = "arg_rating"
        private const val ARG_DATE = "arg_date"

        /**
         * Crea una nueva instancia de DetailFragment.
         * @param id El ID de la película.
         * @param title El título de la película.
         * @param poster La URL del póster de la película.
         * @param overview La sinopsis de la película.
         * @param rating La calificación de la película.
         * @param date La fecha de lanzamiento de la película.
         * @return Una nueva instancia de DetailFragment.
         */
        fun newInstance(id: Int, title: String, poster: String?, overview: String?, rating: Double, date: String?): DetailFragment {
            val f = DetailFragment()
            f.arguments = Bundle().apply {
                putInt(ARG_ID, id)
                putString(ARG_TITLE, title)
                putString(ARG_POSTER, poster)
                putString(ARG_OVERVIEW, overview)
                putDouble(ARG_RATING, rating)
                putString(ARG_DATE, date)
            }
            return f
        }
    }

    private lateinit var btnAddList: Button
    private lateinit var dao: MiListaDao
    private var userId: Int = -1

    private val vm: DetailViewModel by lazy {
        ViewModelProvider(this)[DetailViewModel::class.java]
    }

    /**
     * Se llama cuando se crea el fragmento.
     * @param savedInstanceState Si el fragmento se está recreando a partir de un estado guardado anteriormente, este es el estado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = MiListaDao(requireContext())
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        userId = prefs.getInt("USER_ID", -1)

        // Cargar argumentos a VM si aún no están
        if (!vm.initialized) {
            vm.movieId = requireArguments().getInt(ARG_ID)
            vm.movieTitle = requireArguments().getString(ARG_TITLE) ?: ""
            vm.moviePoster = requireArguments().getString(ARG_POSTER) ?: ""
            vm.movieOverview = requireArguments().getString(ARG_OVERVIEW)
            vm.movieRating = requireArguments().getDouble(ARG_RATING)
            vm.movieDate = requireArguments().getString(ARG_DATE)
            vm.initialized = true
        }
    }

    /**
     * Se llama para que el fragmento instancie su vista de interfaz de usuario.
     * @param inflater El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista principal a la que se debe adjuntar la interfaz de usuario del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     * @return Devuelve la Vista para la interfaz de usuario del fragmento, o nulo.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    /**
     * Se llama inmediatamente después de que onCreateView() haya devuelto, pero antes de que se haya restaurado cualquier estado guardado en la vista.
     * @param view La vista devuelta por onCreateView().
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ocultar bottom nav
        (activity as? MainActivity)?.showBottomNav(false)

        val ivPoster = view.findViewById<ImageView>(R.id.ivDetailPoster)
        val tvTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val tvRating = view.findViewById<TextView>(R.id.tvDetailRating)
        val tvDate = view.findViewById<TextView>(R.id.tvDetailDate)
        val tvOverview = view.findViewById<TextView>(R.id.tvDetailOverview)
        btnAddList = view.findViewById(R.id.btnAddList)

        tvTitle.text = vm.movieTitle
        tvRating.text = "★ ${'$'}{vm.movieRating}"
        tvDate.text = vm.movieDate ?: getString(R.string.unknown_date)
        tvOverview.text = vm.movieOverview ?: getString(R.string.no_overview)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500${'$'}{vm.moviePoster}")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(ivPoster)

        // Estado inicial
        if (!vm.knownAddedState) {
            checkIfMovieIsAdded()
        } else {
            updateButtonUI()
        }

        btnAddList.setOnClickListener {
            if (vm.isAdded) eliminarDeLista() else anadirALista()
        }
    }

    /**
     * Se llama cuando el fragmento se vuelve visible para el usuario.
     */
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }

    /**
     * Se llama cuando la vista asociada con el fragmento está siendo destruida.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNav(true)
    }

    /**
     * Comprueba si la película ya está en la lista del usuario.
     */
    private fun checkIfMovieIsAdded() {
        Thread {
            val exists = dao.existePelicula(userId, vm.movieId)
            requireActivity().runOnUiThread {
                vm.isAdded = exists
                vm.knownAddedState = true
                updateButtonUI()
            }
        }.start()
    }

    /**
     * Actualiza la interfaz de usuario del botón de añadir/eliminar de la lista.
     */
    private fun updateButtonUI() {
        if (!isAdded) return
        if (vm.isAdded) {
            btnAddList.text = getString(R.string.remove_from_list)
            btnAddList.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_red_dark)
        } else {
            btnAddList.text = getString(R.string.add_to_list)
            btnAddList.backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_blue_light)
        }
    }

    /**
     * Añade la película a la lista del usuario.
     */
    private fun anadirALista() {
        Thread {
            val result = dao.agregarPelicula(userId, vm.movieId, vm.movieTitle, vm.moviePoster)
            requireActivity().runOnUiThread {
                if (result != -1L) {
                    Toast.makeText(requireContext(), getString(R.string.saved_to_favs), Toast.LENGTH_SHORT).show()
                    vm.isAdded = true
                    updateButtonUI()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    /**
     * Elimina la película de la lista del usuario.
     */
    private fun eliminarDeLista() {
        Thread {
            val rows = dao.eliminarPelicula(userId, vm.movieId)
            requireActivity().runOnUiThread {
                if (rows > 0) {
                    Toast.makeText(requireContext(), getString(R.string.removed_from_favs), Toast.LENGTH_SHORT).show()
                    vm.isAdded = false
                    updateButtonUI()
                }
            }
        }.start()
    }
}

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

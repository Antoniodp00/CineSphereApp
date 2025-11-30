package com.proyecto.cinesphereapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.MiListaDao

/**
 * Fragmento que muestra la lista de películas guardadas por el usuario.
 * Las películas se cargan desde la base de datos local y se muestran en un RecyclerView.
 * Implementa paginación para cargar más películas al hacer scroll.
 */
class MyListFragment : Fragment() {

    private lateinit var rv: RecyclerView
    private var tvEmpty: TextView? = null
    private lateinit var adapter: LocalMovieAdapter

    private var isLoading = false
    private var isLastPage = false
    private var pageSize = 20
    private var currentOffset = 0

    private var userId: Int = -1

    /**
     * Se llama para que el fragmento instancie su vista de interfaz de usuario.
     * @param inflater El LayoutInflater que se puede usar para inflar cualquier vista en el fragmento.
     * @param container Si no es nulo, esta es la vista principal a la que se debe adjuntar la interfaz de usuario del fragmento.
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     * @return Devuelve la Vista para la interfaz de usuario del fragmento, o nulo.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_my_list_fragment, container, false)
    }

    /**
     * Se llama inmediatamente después de que onCreateView() haya devuelto, pero antes de que se haya restaurado cualquier estado guardado en la vista.
     * @param view La vista devuelta por onCreateView().
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rvLocalMovies)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        val layoutManager = GridLayoutManager(context, 2)
        rv.layoutManager = layoutManager

        // Obtener ID del usuario actual
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        userId = prefs.getInt("USER_ID", -1)

        // Estado inicial
        tvEmpty?.text = getString(R.string.loading)
        tvEmpty?.visibility = View.VISIBLE
        rv.visibility = View.GONE

        if (userId == -1) {
            tvEmpty?.text = getString(R.string.login_first)
            return
        }

        adapter = LocalMovieAdapter(emptyList()) { movie ->
            val fragment = LocalDetailFragment.newInstance(
                id = movie.id,
                title = movie.titulo,
                poster = movie.posterPath,
                status = movie.estado
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
        rv.adapter = adapter

        // Endless scroll listener
        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0) return
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val threshold = 6
                if (!isLoading && !isLastPage &&
                    (visibleItemCount + firstVisibleItemPosition + threshold) >= totalItemCount
                ) {
                    loadNextPage()
                }
            }
        })

        // Primera carga
        resetAndLoad()
    }

    /**
     * Reinicia el estado de paginación y carga la primera página de películas.
     */
    private fun resetAndLoad() {
        isLoading = false
        isLastPage = false
        currentOffset = 0
        adapter.setList(emptyList())
        loadPage(0)
    }

    /**
     * Carga la siguiente página de películas.
     */
    private fun loadNextPage() {
        if (isLoading || isLastPage) return
        loadPage(currentOffset)
    }

    /**
     * Carga una página de películas desde la base de datos local.
     * @param offset El desplazamiento desde el que empezar a cargar.
     */
    private fun loadPage(offset: Int) {
        isLoading = true
        Thread {
            val dao = MiListaDao(requireContext())
            val chunk = dao.obtenerListaUsuarioPaginado(userId, pageSize, offset)

            requireActivity().runOnUiThread {
                try {
                    if (offset == 0) {
                        if (chunk.isEmpty()) {
                            rv.visibility = View.GONE
                            tvEmpty?.text = getString(R.string.no_items)
                            tvEmpty?.visibility = View.VISIBLE
                            isLastPage = true
                        } else {
                            rv.visibility = View.VISIBLE
                            tvEmpty?.visibility = View.GONE
                            adapter.setList(chunk)
                            currentOffset += chunk.size
                            if (chunk.size < pageSize) isLastPage = true
                        }
                    } else {
                        adapter.addMovies(chunk)
                        currentOffset += chunk.size
                        if (chunk.size < pageSize) isLastPage = true
                    }
                } finally {
                    isLoading = false
                }
            }
        }.start()
    }

    /**
     * Se llama cuando el fragmento se vuelve visible para el usuario.
     * Refresca la lista de películas por si ha habido cambios.
     */
    override fun onResume() {
        super.onResume()
        // Refresca al volver por si se actualizó o eliminó algo en el detalle
        resetAndLoad()
    }
}
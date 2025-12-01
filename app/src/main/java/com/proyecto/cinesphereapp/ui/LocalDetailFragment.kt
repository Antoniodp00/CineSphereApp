package com.proyecto.cinesphereapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.MiListaDao

/**
 * Fragmento para ver y modificar los detalles de una película guardada localmente.
 */
class LocalDetailFragment : Fragment() {

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_POSTER = "arg_poster"
        private const val ARG_STATUS = "arg_status"

        /**
         * Crea una nueva instancia de LocalDetailFragment.
         * @param id El ID de la película.
         * @param title El título de la película.
         * @param poster La ruta del póster de la película.
         * @param status El estado actual de la película (e.g., "PENDIENTE", "VISTO").
         * @return Una nueva instancia de LocalDetailFragment.
         */
        fun newInstance(id: Int, title: String, poster: String?, status: String): LocalDetailFragment {
            val f = LocalDetailFragment()
            f.arguments = Bundle().apply {
                putInt(ARG_ID, id)
                putString(ARG_TITLE, title)
                putString(ARG_POSTER, poster)
                putString(ARG_STATUS, status)
            }
            return f
        }
    }

    private lateinit var dao: MiListaDao
    private var userId: Int = -1
    private var movieId: Int = 0

    private val estados = listOf("PENDIENTE", "VIENDO", "VISTO", "ABANDONADA")

    /**
     * Se llama cuando se crea el fragmento.
     * @param savedInstanceState Si el fragmento se está recreando a partir de un estado guardado anteriormente, este es el estado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = MiListaDao(requireContext())
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        userId = prefs.getInt("USER_ID", -1)
        movieId = requireArguments().getInt(ARG_ID)
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
        return inflater.inflate(R.layout.fragment_local_detail, container, false)
    }

    /**
     * Se llama inmediatamente después de que onCreateView() haya devuelto, pero antes de que se haya restaurado cualquier estado guardado en la vista.
     * @param view La vista devuelta por onCreateView().
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = requireArguments().getString(ARG_TITLE) ?: ""
        val posterPath = requireArguments().getString(ARG_POSTER) ?: ""
        val currentStatus = requireArguments().getString(ARG_STATUS) ?: "PENDIENTE"

        val ivPoster = view.findViewById<ImageView>(R.id.ivLocalPoster)
        val tvTitle = view.findViewById<TextView>(R.id.tvLocalTitle)
        val spEstado = view.findViewById<Spinner>(R.id.spEstado)
        val btnUpdate = view.findViewById<Button>(R.id.btnUpdate)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        tvTitle.text = title
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$posterPath")
            .into(ivPoster)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, estados)
        spEstado.adapter = adapter
        val spinnerPosition = adapter.getPosition(currentStatus)
        spEstado.setSelection(spinnerPosition)

        btnUpdate.setOnClickListener {
            val nuevoEstado = spEstado.selectedItem.toString()
            Thread {
                dao.actualizarEstado(userId, movieId, nuevoEstado)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Estado actualizado a: $nuevoEstado", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }.start()
        }

        btnDelete.setOnClickListener {
            confirmarEliminacion()
        }
    }

    /**
     * Se llama cuando el fragmento se vuelve visible para el usuario.
     */
    override fun onResume() {
        super.onResume()
    }

    /**
     * Se llama cuando la vista asociada con el fragmento está siendo destruida.
     */
    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * Muestra un diálogo de confirmación para eliminar la película de la lista del usuario.
     */
    private fun confirmarEliminacion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Película")
            .setMessage("¿Estás seguro de que quieres quitarla de tu lista?")
            .setPositiveButton("Eliminar") { _, _ ->
                Thread {
                    dao.eliminarPelicula(userId, movieId)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Película eliminada", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                }.start()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

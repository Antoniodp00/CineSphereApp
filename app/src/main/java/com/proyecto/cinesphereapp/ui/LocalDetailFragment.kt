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

class LocalDetailFragment : Fragment() {

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_POSTER = "arg_poster"
        private const val ARG_STATUS = "arg_status"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = MiListaDao(requireContext())
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        userId = prefs.getInt("USER_ID", -1)
        movieId = requireArguments().getInt(ARG_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_local_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.showBottomNav(false)

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

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNav(true)
    }

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

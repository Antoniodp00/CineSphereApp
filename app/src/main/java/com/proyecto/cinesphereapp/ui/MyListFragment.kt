package com.proyecto.cinesphereapp.ui

import android.content.Context
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

class MyListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout específico para este fragmento
        return inflater.inflate(R.layout.activity_my_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvLocalMovies)
        // Asegúrate de que tu XML tiene un TextView con id tvEmpty para mostrar mensaje si no hay pelis
        // Si no lo tienes en el XML que subiste, puedes quitar estas líneas del tvEmpty por ahora
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        rv.layoutManager = GridLayoutManager(context, 2)

        // 1. Obtener ID del usuario actual desde SharedPreferences
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)

        // 2. Consultar base de datos
        val dao = MiListaDao(requireContext())
        val lista = dao.obtenerListaUsuario(userId)

        // 3. Mostrar datos o mensaje de vacío
        if (lista.isEmpty()) {
            rv.visibility = View.GONE
            tvEmpty?.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tvEmpty?.visibility = View.GONE
            rv.adapter = LocalMovieAdapter(lista)
        }
    }

    // Método para recargar la lista si volvemos a esta pantalla (opcional pero recomendado)
    override fun onResume() {
        super.onResume()
        // Aquí podrías volver a llamar a la carga de datos si quieres que se actualice al volver
    }
}
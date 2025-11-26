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
        // Inflamos un layout simple que solo tenga un RecyclerView
        // Puedes crear 'fragment_simple_list.xml' o hacerlo programáticamente.
        // Aquí usaremos un layout XML nuevo para ser ordenados.
        return inflater.inflate(R.layout.fragment_mylist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.rvLocalMovies)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty)

        rv.layoutManager = GridLayoutManager(context, 2)

        // 1. Obtener ID del usuario actual
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        val userId = prefs.getInt("USER_ID", -1)

        // 2. Consultar base de datos
        val dao = MiListaDao(requireContext())
        val lista = dao.obtenerListaUsuario(userId)

        // 3. Mostrar datos
        if (lista.isEmpty()) {
            rv.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
            rv.adapter = LocalMovieAdapter(lista)
        }
    }
}
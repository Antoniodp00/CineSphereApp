package com.proyecto.cinesphereapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.proyecto.cinesphereapp.R

/**
 * Fragmento para la pantalla de ajustes.
 * Permite al usuario cerrar sesión y ver información "Acerca de".
 */
class SettingsFragment : Fragment() {

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
        return inflater.inflate(R.layout.activity_settings_fragment, container, false)
    }

    /**
     * Se llama inmediatamente después de que onCreateView() haya devuelto, pero antes de que se haya restaurado cualquier estado guardado en la vista.
     * @param view La vista devuelta por onCreateView().
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogout = view.findViewById<TextView>(R.id.tvLogout)
        val btnAbout = view.findViewById<TextView>(R.id.tvAbout)

        // Lógica de Cerrar Sesión
        btnLogout.setOnClickListener {
            mostrarDialogoLogout()
        }

        // Lógica de "Acerca de"
        btnAbout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("CineSphere App")
                .setMessage("Versión 1.0\\nDesarrollado para Proyecto DAM.\\n\\nUtiliza TMDB API.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    /**
     * Muestra un diálogo de confirmación para cerrar sesión.
     */
    private fun mostrarDialogoLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { _, _ ->
                realizarLogout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Realiza el proceso de cierre de sesión.
     * Borra los datos de sesión de SharedPreferences, navega a la LoginActivity y finaliza la actividad actual.
     */
    private fun realizarLogout() {
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply() // Borra todos los datos de sesión

        val intent = Intent(requireContext(), LoginActivity::class.java)
        // Banderas para crear una tarea nueva y borrar la anterior (el usuario no puede volver con "Atrás")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

        // Finalizamos la actividad contenedora actual
        activity?.finish()
    }
}
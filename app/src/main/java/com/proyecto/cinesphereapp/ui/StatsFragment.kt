package com.proyecto.cinesphereapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.MiListaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Fragmento para mostrar estadísticas de películas del usuario.
 */
class StatsFragment : Fragment() {

    private lateinit var tvTotal: TextView
    private lateinit var dao: MiListaDao
    private var userId: Int = -1

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
        return inflater.inflate(R.layout.activity_stats_fragment, container, false)
    }

    /**
     * Se llama inmediatamente después de que onCreateView() haya devuelto, pero antes de que se haya restaurado cualquier estado guardado en la vista.
     * @param view La vista devuelta por onCreateView().
     * @param savedInstanceState Si no es nulo, este fragmento se está reconstruyendo a partir de un estado guardado anterior.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar DAO y Usuario
        dao = MiListaDao(requireContext())
        val prefs = requireContext().getSharedPreferences("CineSpherePrefs", Context.MODE_PRIVATE)
        userId = prefs.getInt("USER_ID", -1)

        // Referencias a las vistas
        tvTotal = view.findViewById(R.id.tvTotalCount)

        // Cargar datos
        if (userId != -1) {
            cargarEstadisticas(view)
        }
    }

    /**
     * Carga las estadísticas de películas del usuario de forma asíncrona.
     * Utiliza corrutinas para obtener los datos de la base de datos en un hilo de E/S y luego actualiza la interfaz de usuario en el hilo principal.
     * @param view La vista del fragmento.
     */
    private fun cargarEstadisticas(view: View) {
        // Lanzamos una corrutina en el ciclo de vida del fragmento
        lifecycleScope.launch(Dispatchers.IO) {
            // 1. Obtener datos en hilo secundario (IO)
            val total = dao.contarPeliculas(userId)
            val vistos = dao.contarPorEstado(userId, "VISTO")
            val pendientes = dao.contarPorEstado(userId, "PENDIENTE")
            val viendo = dao.contarPorEstado(userId, "VIENDO")

            // 2. Cambiar al hilo principal para tocar la UI
            withContext(Dispatchers.Main) {
                tvTotal.text = total.toString()

                // Actualizar filas (usando los includes)
                setupRow(view.findViewById(R.id.rowVisto), "Vistas", vistos)
                setupRow(view.findViewById(R.id.rowPendiente), "Pendientes", pendientes)
                setupRow(view.findViewById(R.id.rowViendo), "Viendo ahora", viendo)
            }
        }
    }

    /**
     * Configura una fila de estadísticas con una etiqueta y un valor.
     * @param rowView La vista de la fila.
     * @param label La etiqueta de la estadística.
     * @param count El valor de la estadística.
     */
    private fun setupRow(rowView: View, label: String, count: Int) {
        val tvLabel = rowView.findViewById<TextView>(R.id.tvStatLabel)
        val tvValue = rowView.findViewById<TextView>(R.id.tvStatValue)
        tvLabel.text = label
        tvValue.text = count.toString()
    }

    /**
     * Se llama cuando el fragmento se vuelve visible para el usuario.
     * Recarga las estadísticas por si hubo cambios en "Mi Lista".
     */
    override fun onResume() {
        super.onResume()
        if (userId != -1) cargarEstadisticas(requireView())
    }
}
package com.proyecto.cinesphereapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proyecto.cinesphereapp.R

/**
 * Actividad principal que aloja los fragmentos y la navegación inferior.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    /**
     * Se llama cuando se crea la actividad.
     * @param savedInstanceState Si la actividad se está recreando a partir de un estado guardado anteriormente, este es el estado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNav)

        // Cargar el fragmento de INICIO (Explorar) por defecto al abrir la app
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        // Configurar el listener para los clics en el menú
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()      // Explorar (API)
                R.id.nav_mylist -> MyListFragment()  // Mi Lista (Local)
                R.id.nav_stats -> StatsFragment()    // Estadísticas
                R.id.nav_settings -> SettingsFragment() // Ajustes
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    /**
     * Muestra u oculta la barra de navegación inferior.
     * @param show True para mostrar, false para ocultar.
     */
    fun showBottomNav(show: Boolean) {
        bottomNav.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Reemplaza el fragmento actual en el contenedor de fragmentos.
     * @param fragment El fragmento a mostrar.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
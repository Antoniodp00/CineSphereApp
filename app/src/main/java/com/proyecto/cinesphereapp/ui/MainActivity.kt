package com.proyecto.cinesphereapp.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proyecto.cinesphereapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

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

    fun showBottomNav(show: Boolean) {
        bottomNav.visibility = if (show) View.VISIBLE else View.GONE
    }

    /**
     * Método auxiliar para reemplazar el fragmento actual en la pantalla.
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
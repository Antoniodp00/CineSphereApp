package com.proyecto.cinesphereapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.cinesphereapp.MainActivity

import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.UsuarioDao

class LoginActivity : AppCompatActivity() {

    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usuarioDao = UsuarioDao(this)

        val etUser = findViewById<EditText>(R.id.etLoginUser)
        val etPass = findViewById<EditText>(R.id.etLoginPass)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvGoToRegister)

        // Botón Login
        btnLogin.setOnClickListener {
            val user = etUser.text.toString()
            val pass = etPass.text.toString()

            val userId = usuarioDao.login(user, pass)

            if (userId != null) {
                // Guardar sesión (simple)
                val prefs = getSharedPreferences("CineSpherePrefs", MODE_PRIVATE)
                prefs.edit().putInt("USER_ID", userId).apply()

                // Ir a la pantalla principal
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Cierra el login para no volver con "Atrás"
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        // Enlace a Registro
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
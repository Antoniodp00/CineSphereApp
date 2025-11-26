package com.proyecto.cinesphereapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.UsuarioDao

class RegisterActivity : AppCompatActivity() {

    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializamos el DAO
        usuarioDao = UsuarioDao(this)

        val etUser = findViewById<EditText>(R.id.etRegUser)
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPass = findViewById<EditText>(R.id.etRegPass)
        val btnRegister = findViewById<Button>(R.id.btnDoRegister)

        btnRegister.setOnClickListener {
            val user = etUser.text.toString()
            val email = etEmail.text.toString()
            val pass = etPass.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Rellena los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ejecutamos la inserción
            val id = usuarioDao.registrar(user, email, pass)

            if (id != -1L) {
                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_LONG).show()
                finish() // Cerramos la pantalla para volver al Login
            } else {
                Toast.makeText(this, "Error: El usuario ya existe", Toast.LENGTH_LONG).show()
            }
        }
    }
}
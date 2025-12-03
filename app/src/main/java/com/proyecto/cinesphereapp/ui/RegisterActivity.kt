package com.proyecto.cinesphereapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.proyecto.cinesphereapp.R
import com.proyecto.cinesphereapp.data.db.UsuarioDao

/**
 * Actividad para el registro de nuevos usuarios.
 * Permite a un usuario crear una nueva cuenta proporcionando un nombre de usuario, correo electrónico y contraseña.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var usuarioDao: UsuarioDao

    /**
     * Se llama cuando se crea la actividad.
     * Inicializa la interfaz de usuario, la base de datos y configura el listener del botón de registro.
     * @param savedInstanceState Si la actividad se está recreando a partir de un estado guardado anteriormente, este es el estado.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicializamos el DAO
        usuarioDao = UsuarioDao(this)

        val etUser = findViewById<TextInputEditText>(R.id.etRegUser)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegEmail)
        val etPass = findViewById<TextInputEditText>(R.id.etRegPass)
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
package com.example.controlasistencias.cliente

import com.example.controlasistencias.servidor.EmpleadoDAO

import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.io.DataInputStream
import java.io.DataOutputStream
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class ClienteController : Application() {
    private lateinit var primaryStage: Stage  // Guardamos la referencia para cambiar escenas

    override fun start(primaryStage: Stage) {
        this.primaryStage = primaryStage  // Almacena la ventana principal
        mostrarLogin()  // Muestra la pantalla de inicio de sesión
    }


    private fun mostrarLogin() {
        val dbConnection = EmpleadoDAO()

        val usernameField = TextField().apply { promptText = "Username" }
        val passwordField = PasswordField().apply { promptText = "Password" }
        val loginButton = Button("Login")
        val registerButton = Button("Register")
        val errorLabel = Label().apply { style = "-fx-text-fill: red;" }

        loginButton.setOnAction {
            val username = usernameField.text.trim()
            val password = passwordField.text.trim()

            if (dbConnection.autenticarEmpleado(username, password)) {
                enviarMensajeServidor(username)
                mostrarPantallaPrincipal()  // Muestra la pantalla principal después del login
            } else {
                errorLabel.text = "❌ Credenciales incorrectas"
            }
        }

        registerButton.setOnAction {
            mostrarRegistro()  // Cambia a la vista de registro
        }

        val layout = VBox(10.0, usernameField, passwordField, loginButton, registerButton, errorLabel)
        layout.alignment = Pos.CENTER

        cambiarEscena(Scene(layout, 400.0, 300.0), "Control de Asistencia - Login")
    }

    /**
     * Muestra la pantalla de registro de usuario.
     */
    private fun mostrarRegistro() {
        val dbConnection = EmpleadoDAO()

        val nameField = TextField().apply { promptText = "Nombre" }
        val emailField = TextField().apply { promptText = "Correo" }
        val passwordField = PasswordField().apply { promptText = "Password" }
        val registerButton = Button("Registrar")
        val backButton = Button("Volver")
        val errorLabel = Label().apply { style = "-fx-text-fill: red;" }

        registerButton.setOnAction {
            val name = nameField.text.trim()
            val email = emailField.text.trim()
            val password = passwordField.text.trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (dbConnection.registrarEmpleado(name, email, password)) {
                    errorLabel.text = "✅ Usuario registrado con éxito"
                    errorLabel.style = "-fx-text-fill: green;"
                } else {
                    errorLabel.text = "❌ Error: El correo ya está en uso"
                    errorLabel.style = "-fx-text-fill: red;"
                }
            } else {
                errorLabel.text = "⚠ Todos los campos son obligatorios"
            }
        }

        backButton.setOnAction {
            mostrarLogin()  // Regresa a la pantalla de login sin usar start()
        }

        val layout = VBox(10.0, nameField, emailField, passwordField, registerButton, backButton, errorLabel)
        layout.alignment = Pos.CENTER

        cambiarEscena(Scene(layout, 400.0, 350.0), "Registro de Usuario")
    }

    /**
     * Muestra la pantalla principal después del login.
     */
    private fun mostrarPantallaPrincipal() {
        val label = Label("Bienvenido al sistema de Control de Asistencia")
        val logoutButton = Button("Cerrar Sesión")

        logoutButton.setOnAction {
            mostrarLogin()  // Regresa a la pantalla de login al cerrar sesión
        }

        val layout = VBox(20.0, label, logoutButton)
        layout.alignment = Pos.CENTER

        cambiarEscena(Scene(layout, 500.0, 400.0), "Pantalla Principal")
    }


    private fun cambiarEscena(scene: Scene, titulo: String) {
        primaryStage.scene = scene
        primaryStage.title = titulo
        primaryStage.show()
    }

    /**
     * Envía un mensaje al servidor con el nombre de usuario autenticado.
     */
    private fun enviarMensajeServidor(username: String) {
        try {
            val socket = SSLSocketFactory.getDefault().createSocket("localhost", 6000) as SSLSocket
            val salida = DataOutputStream(socket.getOutputStream())
            val entrada = DataInputStream(socket.getInputStream())

            salida.writeUTF(username)
            println("Mensaje enviado al servidor: $username")
            println("Respuesta del servidor: " + entrada.readUTF())

            entrada.close()
            salida.close()
            socket.close()
        } catch (e: Exception) {
            println("❌ Error en la conexión con el servidor: ${e.message}")
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ClienteController::class.java)
        }
    }
}

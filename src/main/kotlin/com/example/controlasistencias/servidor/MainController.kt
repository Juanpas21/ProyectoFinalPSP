package com.example.controlasistencias.servidor

import javafx.fxml.FXML
import javafx.scene.control.Label

class MainController {
    fun main() {
        val servidor = ServidorSSL()
        servidor.iniciarServidor()
    }
}
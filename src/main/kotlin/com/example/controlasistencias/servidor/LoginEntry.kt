package com.example.controlasistencias.servidor



import javafx.beans.property.SimpleStringProperty

class LoginEntry(nombre: String, fecha: String) {
    private val nombre = SimpleStringProperty(nombre)
    private val fecha = SimpleStringProperty(fecha)

    fun nombreProperty() = nombre
    fun fechaProperty() = fecha
}

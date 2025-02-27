package com.example.controlasistencias.servidor



class Autenticacion {
    private val empleadoDAO = EmpleadoDAO()

    fun login(nombre: String, password: String): Boolean {
        return empleadoDAO.autenticarEmpleado(nombre, password)
    }
}

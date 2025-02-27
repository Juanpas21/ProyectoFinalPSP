package com.example.controlasistencias.servidor

import java.sql.Connection
import java.sql.DriverManager


object DatabaseConnection {
    private const val URL = "jdbc:mysql://localhost:3306/control_asistencia"
    private const val USER = "root"
    private const val PASSWORD = ""

    init {
        Class.forName("com.mysql.cj.jdbc.Driver")
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection(URL, USER, PASSWORD)
    }
}


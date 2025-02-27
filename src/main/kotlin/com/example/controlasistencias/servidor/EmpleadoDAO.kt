package com.example.controlasistencias.servidor

import at.favre.lib.crypto.bcrypt.BCrypt
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class EmpleadoDAO {
    // Registrar un nuevo empleado en la base de datos
    // ✅ Registrar un nuevo empleado en la base de datos
    fun registrarEmpleado(nombre: String, correo: String, password: String): Boolean {
        val conn: Connection = DatabaseConnection.getConnection()

        // Verificar si el correo ya existe
        if (existeCorreo(correo)) {
            println("❌ Error: El correo ya está registrado.")
            conn.close()
            return false // Evita duplicados
        }

        val query = "INSERT INTO empleados (nombre, correo, password) VALUES (?, ?, ?)"
        val stmt: PreparedStatement = conn.prepareStatement(query)

        // Hash de la contraseña con BCrypt
        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        stmt.setString(1, nombre)
        stmt.setString(2, correo)
        stmt.setString(3, hashedPassword)

        val resultado = stmt.executeUpdate() > 0

        stmt.close()
        conn.close()
        return resultado
    }

    // ✅ Verifica si un correo ya está registrado (para evitar duplicados)
    private fun existeCorreo(correo: String): Boolean {
        val conn: Connection = DatabaseConnection.getConnection()
        val query = "SELECT COUNT(*) FROM empleados WHERE correo = ?"
        val stmt: PreparedStatement = conn.prepareStatement(query)

        stmt.setString(1, correo)
        val rs: ResultSet = stmt.executeQuery()
        var existe = false

        if (rs.next()) {
            existe = rs.getInt(1) > 0
        }

        rs.close()
        stmt.close()
        conn.close()
        return existe
    }

    // ✅ Autenticación de usuarios usando `BCrypt`
    fun autenticarEmpleado(nombre: String, password: String): Boolean {
        val conn: Connection = DatabaseConnection.getConnection()
        val query = "SELECT password FROM empleados WHERE nombre = ?"
        val stmt: PreparedStatement = conn.prepareStatement(query)

        stmt.setString(1, nombre)
        val rs: ResultSet = stmt.executeQuery()

        if (rs.next()) {
            val storedHash = rs.getString("password")

            // Verificar la contraseña con BCrypt
            val result = BCrypt.verifyer().verify(password.toCharArray(), storedHash)

            // Si la verificación es correcta, registrar el login en la base de datos
            if (result.verified) {
                registrarLogin(nombre)
            }

            rs.close()
            stmt.close()
            conn.close()
            return result.verified
        }

        rs.close()
        stmt.close()
        conn.close()
        return false
    }

    // ✅ Registrar la hora de inicio de sesión de un usuario en la tabla `logins`
    fun registrarLogin(nombre: String) {
        val conn: Connection = DatabaseConnection.getConnection()
        val query = "INSERT INTO logins (nombre) VALUES (?)"
        val stmt: PreparedStatement = conn.prepareStatement(query)

        stmt.setString(1, nombre)
        stmt.executeUpdate()

        stmt.close()
        conn.close()
    }

    // ✅ Obtener todos los logins registrados con su fecha y hora
    fun obtenerLogins(): List<Pair<String, String>> {
        val conn: Connection = DatabaseConnection.getConnection()
        val query = "SELECT nombre, fecha FROM logins ORDER BY fecha DESC"
        val stmt: PreparedStatement = conn.prepareStatement(query)

        val rs: ResultSet = stmt.executeQuery()
        val listaLogins = mutableListOf<Pair<String, String>>()

        while (rs.next()) {
            val nombre = rs.getString("nombre")
            val fecha = rs.getString("fecha")
            listaLogins.add(Pair(nombre, fecha))
        }

        rs.close()
        stmt.close()
        conn.close()
        return listaLogins
    }
}

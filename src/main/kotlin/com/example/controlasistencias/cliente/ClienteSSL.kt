package com.example.controlasistencias.cliente

import java.io.*
import java.security.KeyStore
import java.util.*
import javax.net.ssl.*

class ClienteSSL {
    fun registrarAsistencia(nombre: String) {
        val sslSocketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
        val socket = sslSocketFactory.createSocket("localhost", 6000)
        val output = PrintWriter(socket.getOutputStream(), true)
        val input = Scanner(socket.getInputStream())

        output.println(nombre)
        println(input.nextLine())
        socket.close()
    }
}
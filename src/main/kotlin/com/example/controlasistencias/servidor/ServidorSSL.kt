package com.example.controlasistencias.servidor

import java.io.*
import java.security.KeyStore
import javax.net.ssl.*

class ServidorSSL {
    private val port = 6000

    fun iniciarServidor() {
        val almacenClaves = KeyStore.getInstance("JKS")
        almacenClaves.load(FileInputStream("resources/Madness.jks"), "1234567".toCharArray())

        val manager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
        manager.init(almacenClaves, "1234567".toCharArray())

        val confianza = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        confianza.init(almacenClaves)

        val contexto = SSLContext.getInstance("TLS")
        contexto.init(manager.keyManagers, confianza.trustManagers, null)

        val socketFabrica = contexto.serverSocketFactory
        val servidorSSL = socketFabrica.createServerSocket(port) as SSLServerSocket
        servidorSSL.useClientMode = false

        println("Servidor SSL iniciado en el puerto $port")

        while (true) {
            val cliente: SSLSocket = servidorSSL.accept() as SSLSocket
            Thread {
                try {
                    val entrada = BufferedReader(InputStreamReader(cliente.inputStream))
                    val mensaje = entrada.readLine()
                    println("Mensaje recibido: $mensaje")

                    cliente.close()
                } catch (e: Exception) {
                    println("Error en la conexión con el cliente: ${e.message}")
                }
            }.start()
        }
    }
}

fun main() {
    val servidor = ServidorSSL()
    servidor.iniciarServidor()
}

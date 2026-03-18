package com.example.parcial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.parcial.ui.theme.DashboardVista
import com.example.parcial.ui.theme.ListadoReservasVista
import com.example.parcial.ui.theme.MiPrimeraVista
import com.example.parcial.ui.theme.ParcialTheme

sealed class Pantalla {
    object Dashboard : Pantalla()
    object NuevaReserva : Pantalla()
    object ListadoReservas : Pantalla()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParcialTheme {
                
                // --- ESTADO GLOBAL (Nuestra "Base de Datos") ---
                var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Dashboard) }
                
                // Listas mutables reactivas
                val listaPersonas = remember { mutableStateListOf<Persona>() }
                val listaCanchas = remember { 
                    mutableStateListOf(
                        Cancha(nombre = "Hoyo 1", tipo = "Pasto"),
                        Cancha(nombre = "Hoyo 2", tipo = "Sintética"),
                        Cancha(nombre = "Hoyo 3", tipo = "Pasto")
                    )
                }
                val listaReservas = remember { mutableStateListOf<Reserva>() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        is Pantalla.Dashboard -> DashboardVista(
                            modifier = Modifier.padding(innerPadding),
                            proximasReservas = listaReservas.map { "${it.cliente.nombre} - ${it.hora} - ${it.cancha.nombre}" },
                            onNuevaReserva = { pantallaActual = Pantalla.NuevaReserva },
                            onListadoReservas = { pantallaActual = Pantalla.ListadoReservas }
                        )
                        
                        is Pantalla.NuevaReserva -> MiPrimeraVista(
                            modifier = Modifier.padding(innerPadding),
                            onGuardar = { nombre, hora, canchaNombre ->
                                // 1. Creamos o buscamos a la persona
                                val nuevaPersona = Persona(nombre = nombre, telefono = "Sin especificar")
                                listaPersonas.add(nuevaPersona)
                                
                                // 2. Buscamos la cancha (o creamos una genérica)
                                val canchaSeleccionada = listaCanchas.find { it.nombre == canchaNombre } 
                                    ?: Cancha(nombre = canchaNombre, tipo = "Estándar")
                                
                                // 3. Creamos la reserva lógica
                                val nuevaReserva = Reserva(
                                    cliente = nuevaPersona,
                                    cancha = canchaSeleccionada,
                                    fecha = "12/05/2026",
                                    hora = hora
                                )
                                
                                listaReservas.add(nuevaReserva)
                                pantallaActual = Pantalla.Dashboard
                            },
                            onCancelar = { pantallaActual = Pantalla.Dashboard }
                        )
                        
                        is Pantalla.ListadoReservas -> {
                            // Adaptamos nuestra lista de objetos Reserva al formato que espera la vista
                            // Nota: En un futuro podrías actualizar ListadoReservasVista para que acepte directamente List<Reserva> (nuestro nuevo modelo)
                            val reservasParaVista = listaReservas.map { reserva ->
                                com.example.parcial.ui.theme.Reserva(
                                    cliente = reserva.cliente.nombre,
                                    fecha = reserva.fecha,
                                    hora = reserva.hora,
                                    cancha = reserva.cancha.nombre,
                                    estado = reserva.estado
                                )
                            }
                            
                            ListadoReservasVista(
                                modifier = Modifier.padding(innerPadding),
                                reservas = reservasParaVista,
                                onVolver = { pantallaActual = Pantalla.Dashboard }
                            )
                        }
                    }
                }
            }
        }
    }
}

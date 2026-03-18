package com.example.parcial

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.parcial.ui.theme.DashboardVista
import com.example.parcial.ui.theme.ListadoReservasVista
import com.example.parcial.ui.theme.MiPrimeraVista
import com.example.parcial.ui.theme.ParcialTheme
import java.text.SimpleDateFormat
import java.util.*

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
                val context = LocalContext.current
                
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

                // Estado para manejar la edición
                var reservaAEditar by remember { mutableStateOf<Reserva?>(null) }

                // Función para determinar el estado dinámico basado en la hora (Reservas duran 1 hora)
                fun obtenerEstadoReserva(fecha: String, hora: String): String {
                    return try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaInicio = sdf.parse("$fecha $hora") ?: return "Activa"
                        
                        val calendar = Calendar.getInstance()
                        calendar.time = fechaInicio
                        calendar.add(Calendar.HOUR_OF_DAY, 1)
                        val fechaFin = calendar.time
                        
                        val ahora = Date()
                        if (ahora.after(fechaFin)) "Finalizada" else "Activa"
                    } catch (e: Exception) {
                        "Activa"
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        is Pantalla.Dashboard -> DashboardVista(
                            modifier = Modifier.padding(innerPadding),
                            proximasReservas = listaReservas.map { "${it.cliente.nombre} - ${it.hora} - ${it.cancha.nombre}" },
                            onNuevaReserva = { 
                                reservaAEditar = null
                                pantallaActual = Pantalla.NuevaReserva 
                            },
                            onListadoReservas = { pantallaActual = Pantalla.ListadoReservas }
                        )
                        
                        is Pantalla.NuevaReserva -> MiPrimeraVista(
                            modifier = Modifier.padding(innerPadding),
                            reservaAEditar = reservaAEditar,
                            onGuardar = { nombre, telefono, fecha, hora, canchaNombre ->
                                // Validar si ya existe una reserva activa para la misma cancha, fecha y hora
                                // (Excluyendo la propia reserva si estamos editando)
                                val existeReserva = listaReservas.any { 
                                    it.id != reservaAEditar?.id &&
                                    it.cancha.nombre == canchaNombre && 
                                    it.fecha == fecha && 
                                    it.hora == hora && 
                                    obtenerEstadoReserva(it.fecha, it.hora) == "Activa" 
                                }

                                if (!existeReserva) {
                                    if (reservaAEditar != null) {
                                        // ACTUALIZAR RESERVA EXISTENTE
                                        val index = listaReservas.indexOfFirst { it.id == reservaAEditar!!.id }
                                        if (index != -1) {
                                            val canchaSel = listaCanchas.find { it.nombre == canchaNombre } 
                                                ?: Cancha(nombre = canchaNombre, tipo = "Estándar")
                                            
                                            listaReservas[index] = listaReservas[index].copy(
                                                fecha = fecha,
                                                hora = hora,
                                                cancha = canchaSel
                                            )
                                            // Actualizar también los datos de la persona
                                            listaReservas[index].cliente.nombre = nombre
                                            listaReservas[index].cliente.telefono = telefono
                                        }
                                        reservaAEditar = null
                                    } else {
                                        // CREAR NUEVA RESERVA
                                        val nuevaPersona = Persona(nombre = nombre, telefono = telefono)
                                        listaPersonas.add(nuevaPersona)
                                        
                                        val canchaSeleccionada = listaCanchas.find { it.nombre == canchaNombre } 
                                            ?: Cancha(nombre = canchaNombre, tipo = "Estándar")
                                        
                                        val nuevaReserva = Reserva(
                                            cliente = nuevaPersona,
                                            cancha = canchaSeleccionada,
                                            fecha = fecha,
                                            hora = hora
                                        )
                                        listaReservas.add(nuevaReserva)
                                    }
                                    pantallaActual = Pantalla.Dashboard
                                } else {
                                    Toast.makeText(context, "No se puede reservar a esa hora (Cancha ocupada)", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCancelar = { 
                                reservaAEditar = null
                                pantallaActual = Pantalla.Dashboard 
                            }
                        )
                        
                        is Pantalla.ListadoReservas -> {
                            val reservasParaVista = listaReservas.map { reserva ->
                                com.example.parcial.ui.theme.Reserva(
                                    id = reserva.id,
                                    cliente = reserva.cliente.nombre,
                                    fecha = reserva.fecha,
                                    hora = reserva.hora,
                                    cancha = reserva.cancha.nombre,
                                    estado = obtenerEstadoReserva(reserva.fecha, reserva.hora)
                                )
                            }
                            
                            ListadoReservasVista(
                                modifier = Modifier.padding(innerPadding),
                                reservas = reservasParaVista,
                                onVolver = { pantallaActual = Pantalla.Dashboard },
                                onEditar = { id ->
                                    reservaAEditar = listaReservas.find { it.id == id }
                                    pantallaActual = Pantalla.NuevaReserva
                                },
                                onEliminar = { id ->
                                    listaReservas.removeAll { it.id == id }
                                    Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

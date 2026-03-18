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
import kotlinx.coroutines.launch
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
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val dao = db.reservaDao()
                val scope = rememberCoroutineScope()

                // --- ESTADO GLOBAL DESDE SQLITE ---
                var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Dashboard) }
                
                // Observamos las reservas desde la BD
                val listaReservasConDetalles by dao.obtenerTodasLasReservasConDetalles().collectAsState(initial = emptyList())
                val listaReservas = listaReservasConDetalles.map { it.toReservaConDetalles() }
                
                // Las canchas las podemos manejar fijas o desde la BD
                val listaCanchasFijas = listOf(
                    Cancha(nombre = "Hoyo 1", tipo = "Pasto"),
                    Cancha(nombre = "Hoyo 2", tipo = "Sintética"),
                    Cancha(nombre = "Hoyo 3", tipo = "Pasto")
                )

                var reservaAEditarId by remember { mutableStateOf<Int?>(null) }
                val reservaAEditar = if (reservaAEditarId != null) {
                    listaReservas.find { it.id == reservaAEditarId }
                } else null

                fun obtenerEstadoReserva(fecha: String, hora: String): String {
                    return try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaInicio = sdf.parse("$fecha $hora") ?: return "Activa"
                        val calendar = Calendar.getInstance()
                        calendar.time = fechaInicio
                        calendar.add(Calendar.HOUR_OF_DAY, 1)
                        if (Date().after(calendar.time)) "Finalizada" else "Activa"
                    } catch (e: Exception) { "Activa" }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        is Pantalla.Dashboard -> DashboardVista(
                            modifier = Modifier.padding(innerPadding),
                            proximasReservas = listaReservas.map { "${it.cliente.nombre} - ${it.hora} - ${it.cancha.nombre}" },
                            onNuevaReserva = { 
                                reservaAEditarId = null
                                pantallaActual = Pantalla.NuevaReserva 
                            },
                            onListadoReservas = { pantallaActual = Pantalla.ListadoReservas }
                        )
                        
                        is Pantalla.NuevaReserva -> MiPrimeraVista(
                            modifier = Modifier.padding(innerPadding),
                            // Adaptamos el modelo de la BD al modelo que espera la vista
                            reservaAEditar = reservaAEditar?.let { 
                                Reserva(it.id, it.cliente, it.cancha, it.fecha, it.hora, it.estado) 
                            },
                            onGuardar = { nombre, telefono, fecha, hora, canchaNombre ->
                                val existeReserva = listaReservas.any { 
                                    it.id != reservaAEditarId &&
                                    it.cancha.nombre == canchaNombre && 
                                    it.fecha == fecha && 
                                    it.hora == hora && 
                                    obtenerEstadoReserva(it.fecha, it.hora) == "Activa" 
                                }

                                if (!existeReserva) {
                                    scope.launch {
                                        if (reservaAEditarId != null && reservaAEditar != null) {
                                            // 1. Actualizar Persona
                                            val persona = reservaAEditar.cliente.copy(nombre = nombre, telefono = telefono)
                                            dao.actualizarPersona(persona)
                                            
                                            // 2. Buscar cancha
                                            var cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            if (cancha == null) {
                                                cancha = Cancha(nombre = canchaNombre, tipo = "Estándar")
                                                dao.insertarCancha(cancha)
                                                cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            }
                                            
                                            // 3. Actualizar Reserva
                                            val reserva = Reserva(
                                                id = reservaAEditarId!!,
                                                personaId = persona.id,
                                                canchaId = cancha?.id ?: 0,
                                                fecha = fecha,
                                                hora = hora
                                            )
                                            dao.actualizarReserva(reserva)
                                            reservaAEditarId = null
                                        } else {
                                            // 1. Insertar Persona
                                            val personaId = dao.insertarPersona(Persona(nombre = nombre, telefono = telefono))
                                            
                                            // 2. Buscar o Insertar Cancha
                                            var cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            if (cancha == null) {
                                                cancha = Cancha(nombre = canchaNombre, tipo = "Estándar")
                                                dao.insertarCancha(cancha)
                                                cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            }
                                            
                                            // 3. Insertar Reserva
                                            dao.insertarReserva(Reserva(
                                                personaId = personaId.toInt(),
                                                canchaId = cancha?.id ?: 0,
                                                fecha = fecha,
                                                hora = hora
                                            ))
                                        }
                                        pantallaActual = Pantalla.Dashboard
                                    }
                                } else {
                                    Toast.makeText(context, "Cancha ocupada en ese horario", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCancelar = { 
                                reservaAEditarId = null
                                pantallaActual = Pantalla.Dashboard 
                            }
                        )
                        
                        is Pantalla.ListadoReservas -> {
                            ListadoReservasVista(
                                modifier = Modifier.padding(innerPadding),
                                reservas = listaReservas.map { res ->
                                    com.example.parcial.ui.theme.Reserva(
                                        id = res.id,
                                        cliente = res.cliente.nombre,
                                        fecha = res.fecha,
                                        hora = res.hora,
                                        cancha = res.cancha.nombre,
                                        estado = obtenerEstadoReserva(res.fecha, res.hora)
                                    )
                                },
                                onVolver = { pantallaActual = Pantalla.Dashboard },
                                onEditar = { id ->
                                    reservaAEditarId = id
                                    pantallaActual = Pantalla.NuevaReserva
                                },
                                onEliminar = { id ->
                                    scope.launch {
                                        val res = dao.obtenerReservaPorId(id)
                                        if (res != null) dao.eliminarReserva(res)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

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

/**
 * Pantalla: Clase sellada para representar los estados de navegación de la aplicación.
 * Define las rutas o vistas posibles dentro de la aplicación.
 */
sealed class Pantalla {
    object Dashboard : Pantalla()
    object NuevaReserva : Pantalla()
    object ListadoReservas : Pantalla()
}

/**
 * MainActivity: Clase principal de la aplicación.
 * Gestiona el estado global, la navegación entre pantallas y la interacción con Room Database.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Habilita el diseño de borde a borde (edge-to-edge)
        enableEdgeToEdge()
        setContent {
            // Aplicar el tema personalizado de la aplicación
            ParcialTheme {
                // Obtener el contexto y configurar el acceso a la base de datos
                val context = LocalContext.current
                val db = remember { AppDatabase.getDatabase(context) }
                val dao = db.reservaDao()
                // Scope para lanzar corrutinas desde la UI
                val scope = rememberCoroutineScope()

                // Estado de navegación: controla qué pantalla está visible
                var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Dashboard) }
                
                // Observar las reservas con detalles directamente desde la base de datos
                // El uso de Flow garantiza que la UI se actualice automáticamente ante cambios
                val listaReservasConDetallesRaw by dao.obtenerTodasLasReservasConDetalles().collectAsState(initial = emptyList())
                val listaReservasConDetalles = listaReservasConDetallesRaw.map { it.toReservaConDetalles() }
                
                // Estado para el manejo de edición
                var reservaAEditarId by remember { mutableStateOf<Int?>(null) }
                val reservaAEditarDetalle = if (reservaAEditarId != null) {
                    listaReservasConDetalles.find { it.id == reservaAEditarId }
                } else null

                /**
                 * Función auxiliar para determinar si una reserva sigue vigente.
                 * @param fecha Fecha de la reserva (dd/MM/yyyy)
                 * @param hora Hora de la reserva (HH:mm)
                 * @return "Activa" o "Finalizada"
                 */
                fun obtenerEstadoReserva(fecha: String, hora: String): String {
                    return try {
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val fechaInicio = sdf.parse("$fecha $hora") ?: return "Activa"
                        val calendar = Calendar.getInstance()
                        calendar.time = fechaInicio
                        calendar.add(Calendar.HOUR_OF_DAY, 1) // Duración estimada de 1 hora
                        if (Date().after(calendar.time)) "Finalizada" else "Activa"
                    } catch (e: Exception) { "Activa" }
                }

                // Cálculo de estadísticas para el Dashboard
                val hoy = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val numReservasHoy = listaReservasConDetalles.count { it.fecha == hoy }
                val numCanchasOcupadas = listaReservasConDetalles.count { 
                    it.fecha == hoy && obtenerEstadoReserva(it.fecha, it.hora) == "Activa" 
                }

                // Estructura principal con Scaffold para manejo de paddings de sistema
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        // --- VISTA DASHBOARD ---
                        is Pantalla.Dashboard -> DashboardVista(
                            modifier = Modifier.padding(innerPadding),
                            proximasReservas = listaReservasConDetalles
                                .filter { obtenerEstadoReserva(it.fecha, it.hora) == "Activa" }
                                .map { "${it.cliente.nombre} - ${it.hora} (${it.cancha.nombre})" },
                            reservasHoy = numReservasHoy,
                            canchasOcupadas = numCanchasOcupadas,
                            onNuevaReserva = { 
                                reservaAEditarId = null 
                                pantallaActual = Pantalla.NuevaReserva 
                            },
                            onListadoReservas = { pantallaActual = Pantalla.ListadoReservas }
                        )
                        
                        // --- VISTA FORMULARIO (NUEVA / EDITAR) ---
                        is Pantalla.NuevaReserva -> MiPrimeraVista(
                            modifier = Modifier.padding(innerPadding),
                            reservaAEditar = reservaAEditarDetalle,
                            onGuardar = { nombre, telefono, fecha, hora, canchaNombre ->
                                // Validación de disponibilidad: busca colisiones de horario en la misma cancha
                                val existeReserva = listaReservasConDetalles.any { 
                                    it.id != reservaAEditarId && 
                                    it.cancha.nombre == canchaNombre && 
                                    it.fecha == fecha && 
                                    it.hora == hora && 
                                    obtenerEstadoReserva(it.fecha, it.hora) == "Activa" 
                                }

                                if (!existeReserva) {
                                    scope.launch {
                                        if (reservaAEditarId != null && reservaAEditarDetalle != null) {
                                            // Proceso de actualización
                                            val persona = reservaAEditarDetalle.cliente.copy(nombre = nombre, telefono = telefono)
                                            dao.actualizarPersona(persona)
                                            
                                            var cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            if (cancha == null) {
                                                cancha = Cancha(nombre = canchaNombre, tipo = "Estándar")
                                                dao.insertarCancha(cancha)
                                                cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            }
                                            
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
                                            // Proceso de creación
                                            val personaId = dao.insertarPersona(Persona(nombre = nombre, telefono = telefono))
                                            
                                            var cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            if (cancha == null) {
                                                cancha = Cancha(nombre = canchaNombre, tipo = "Estándar")
                                                dao.insertarCancha(cancha)
                                                cancha = dao.obtenerCanchaPorNombre(canchaNombre)
                                            }
                                            
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
                                    Toast.makeText(context, "La cancha ya está reservada para esa hora.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCancelar = { 
                                reservaAEditarId = null
                                pantallaActual = Pantalla.Dashboard 
                            }
                        )
                        
                        // --- VISTA LISTADO ---
                        is Pantalla.ListadoReservas -> {
                            ListadoReservasVista(
                                modifier = Modifier.padding(innerPadding),
                                reservas = listaReservasConDetalles.map { res ->
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
                                    // Eliminación de registro de forma asíncrona
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

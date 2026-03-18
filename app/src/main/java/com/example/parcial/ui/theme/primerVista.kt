package com.example.parcial.ui.theme

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parcial.ReservaConDetalles
import java.text.SimpleDateFormat
import java.util.*

/**
 * MiPrimeraVista: Componente de UI para crear o editar una reserva.
 * @param reservaAEditar Si se proporciona, la vista se precarga con los datos de esta reserva.
 * @param onGuardar Callback que se ejecuta al presionar el botón de guardar.
 * @param onCancelar Callback para volver a la pantalla anterior sin guardar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPrimeraVista(
    modifier: Modifier = Modifier,
    reservaAEditar: ReservaConDetalles? = null,
    onGuardar: (String, String, String, String, String) -> Unit,
    onCancelar: () -> Unit
) {
    val verdeApp = Color(0xFF388E3C)
    val context = LocalContext.current

    // Estados para los campos del formulario, inicializados con los valores de la reserva a editar si existe
    var nombre by remember { mutableStateOf(reservaAEditar?.cliente?.nombre ?: "") }
    var telefono by remember { mutableStateOf(reservaAEditar?.cliente?.telefono ?: "") }
    var fecha by remember { mutableStateOf(reservaAEditar?.fecha ?: "") }
    var hora by remember { mutableStateOf(reservaAEditar?.hora ?: "") }
    var cancha by remember { mutableStateOf(reservaAEditar?.cancha?.nombre ?: "") }
    var cantidadJugadores by remember { mutableIntStateOf(1) }

    // Estados para controlar la visibilidad de los selectores de fecha y hora
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Configuración del DatePicker para restringir la selección a fechas futuras (hoy en adelante)
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val hoyUtc = calendar.timeInMillis

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= hoyUtc
            }
        }
    )
    
    // Configuración inicial del TimePicker
    val timePickerState = rememberTimePickerState(
        initialHour = if (reservaAEditar != null) reservaAEditar.hora.split(":")[0].toInt() else 0,
        initialMinute = if (reservaAEditar != null) reservaAEditar.hora.split(":")[1].toInt() else 0,
        is24Hour = true
    )

    // Diálogo para selección de fecha
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        fecha = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Diálogo para selección de hora
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    hora = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Permite scroll si el formulario es largo
    ) {
        // Barra superior de la pantalla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(verdeApp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "<", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onCancelar() })
            Spacer(modifier = Modifier.width(80.dp))
            Text(
                text = if (reservaAEditar != null) "Editar Reserva" else "Nueva Reserva",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Cuerpo del formulario
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTextoPersonalizado("Nombre del Cliente", nombre, onCambio = { nombre = it })
            
            CampoTextoPersonalizado(
                label = "Teléfono",
                valor = telefono,
                onCambio = { if (it.all { char -> char.isDigit() } && it.length <= 10) telefono = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            
            // Selector de fecha interactivo
            Box(modifier = Modifier.clickable { showDatePicker = true }) {
                CampoTextoPersonalizado(
                    label = "Fecha de Reserva",
                    valor = fecha,
                    onCambio = {},
                    enabled = false,
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )
            }

            // Selector de hora interactivo
            Box(modifier = Modifier.clickable { showTimePicker = true }) {
                CampoTextoPersonalizado(
                    label = "Hora",
                    valor = hora,
                    onCambio = {},
                    enabled = false,
                    readOnly = true
                )
            }

            CampoTextoPersonalizado(
                label = "Número de Cancha (1-5)",
                valor = cancha,
                onCambio = { if (it.isEmpty() || it.all { char -> char.isDigit() }) cancha = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Selector de cantidad de jugadores
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Cantidad de Jugadores", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    FilledIconButton(
                        onClick = { if (cantidadJugadores > 1) cantidadJugadores-- },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = verdeApp)
                    ) {
                        Text("-", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Text(
                        text = cantidadJugadores.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.widthIn(min = 20.dp)
                    )

                    FilledIconButton(
                        onClick = { cantidadJugadores++ },
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = verdeApp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Más")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        // Validación de reglas de negocio antes de guardar
                        val esTelefonoValido = telefono.startsWith("3") && telefono.length == 10 && telefono.all { it.isDigit() }
                        val canchaInt = cancha.toIntOrNull()
                        val esCanchaValida = canchaInt != null && canchaInt in 1..5
                        
                        if (nombre.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank() && cancha.isNotBlank() && telefono.isNotBlank()) {
                            if (!esTelefonoValido) {
                                Toast.makeText(context, "Teléfono inválido. Debe iniciar con 3 y tener 10 dígitos.", Toast.LENGTH_LONG).show()
                            } else if (!esCanchaValida) {
                                Toast.makeText(context, "Número de cancha inválido. Debe ser entre 1 y 5.", Toast.LENGTH_LONG).show()
                            } else {
                                onGuardar(nombre, telefono, fecha, hora, cancha)
                            }
                        } else {
                            Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = verdeApp)
                ) { Text("Guardar") }

                Button(onClick = onCancelar, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                    Text("Cancelar")
                }
            }
        }
    }
}

/**
 * Componente reutilizable para campos de entrada de texto con etiqueta.
 */
@Composable
fun CampoTextoPersonalizado(
    label: String,
    valor: String,
    onCambio: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = enabled,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon
        )
    }
}

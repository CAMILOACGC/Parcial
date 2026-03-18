package com.example.parcial.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiPrimeraVista(
    modifier: Modifier = Modifier,
    onGuardar: (String, String, String, String) -> Unit,
    onCancelar: () -> Unit
) {
    val verdeApp = Color(0xFF388E3C)

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var cancha by remember { mutableStateOf("") }
    var cantidadJugadores by remember { mutableIntStateOf(1) }
    var estado by remember { mutableStateOf("") }

    // Estados para los Diálogos
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = true)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(verdeApp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "<", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onCancelar() })
            Spacer(modifier = Modifier.width(80.dp))
            Text(text = "Nueva Reserva", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTextoPersonalizado("Nombre del Cliente", nombre, onCambio = { nombre = it })
            CampoTextoPersonalizado("Teléfono", telefono, onCambio = { telefono = it })
            
            // CAMPO FECHA
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

            // CAMPO HORA
            Box(modifier = Modifier.clickable { showTimePicker = true }) {
                CampoTextoPersonalizado(
                    label = "Hora",
                    valor = hora,
                    onCambio = {},
                    enabled = false,
                    readOnly = true
                )
            }

            CampoTextoPersonalizado("Número de Cancha", cancha, onCambio = { cancha = it })

            // SECCIÓN CANTIDAD DE JUGADORES CON BOTONES
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

            CampoTextoPersonalizado("Estado", estado, onCambio = { estado = it })

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank() && cancha.isNotBlank()) {
                            onGuardar(nombre, fecha, hora, cancha)
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

@Composable
fun CampoTextoPersonalizado(
    label: String,
    valor: String,
    onCambio: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
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
            trailingIcon = trailingIcon
        )
    }
}

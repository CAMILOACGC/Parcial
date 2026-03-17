package com.example.parcial.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MiPrimeraVista(
    modifier: Modifier = Modifier,
    onGuardar: (String, String, String) -> Unit,
    onCancelar: () -> Unit
) {
    val verdeApp = Color(0xFF388E3C)

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var cancha by remember { mutableStateOf("") }
    var jugadores by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // --- ENCABEZADO ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(verdeApp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "<", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(80.dp))
            Text(
                text = "Nueva Reserva",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- FORMULARIO ---
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTextoPersonalizado("Nombre del Cliente", nombre) { nombre = it }
            CampoTextoPersonalizado("Teléfono", telefono) { telefono = it }
            CampoTextoPersonalizado("Fecha (12/05/2026)", fecha) { fecha = it }
            CampoTextoPersonalizado("Hora (10:00 AM)", hora) { hora = it }
            CampoTextoPersonalizado("Número de Cancha", cancha) { cancha = it }
            CampoTextoPersonalizado("Cantidad de Jugadores", jugadores) { jugadores = it }
            CampoTextoPersonalizado("Estado", estado) { estado = it }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTONES ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && hora.isNotBlank() && cancha.isNotBlank()) {
                            onGuardar(nombre, hora, cancha)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = verdeApp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Guardar")
                }

                Button(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoPersonalizado(label: String, valor: String, onCambio: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
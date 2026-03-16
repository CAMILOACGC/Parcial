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
fun MiPrimeraVista(modifier: Modifier = Modifier) {
    // Definimos el color verde de tu imagen
    val verdeApp = Color(0xFF388E3C)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Por si la pantalla es pequeña y hay muchos campos
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
            CampoTextoPersonalizado("Nombre del Cliente")
            CampoTextoPersonalizado("Teléfono")
            CampoTextoPersonalizado("Fecha (12/05/2026)")
            CampoTextoPersonalizado("Hora (10:00 AM)")
            CampoTextoPersonalizado("Número de Cancha")
            CampoTextoPersonalizado("Cantidad de Jugadores")
            CampoTextoPersonalizado("Estado")

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTONES ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { /* Acción de guardar */ },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = verdeApp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Guardar")
                }

                Button(
                    onClick = { /* Acción de cancelar */ },
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

// Función auxiliar para no repetir código de los campos de texto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTextoPersonalizado(label: String) {
    var texto by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
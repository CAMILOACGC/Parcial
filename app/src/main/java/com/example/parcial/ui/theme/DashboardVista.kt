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

/**
 * DashboardVista: Pantalla principal de la aplicación que muestra un resumen.
 * @param proximasReservas Lista de strings con la información resumida de las reservas.
 * @param onNuevaReserva Acción al presionar el botón de crear reserva.
 * @param onListadoReservas Acción al presionar el botón de ver todas las reservas.
 */
@Composable
fun DashboardVista(
    modifier: Modifier = Modifier,
    proximasReservas: List<String> = emptyList(),
    onNuevaReserva: () -> Unit = {},
    onListadoReservas: () -> Unit = {}
) {
    // Definición de la paleta de colores institucional
    val verdeOscuro = Color(0xFF2E7D32)
    val verdeMedio = Color(0xFF388E3C)
    val verdeClaro = Color(0xFF43A047)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Encabezado con el nombre del club y perfil (estético)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(verdeMedio)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Golf Club", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("👤", fontSize = 20.sp)
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Fila de tarjetas estadísticas rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TarjetaEstadistica(Modifier.weight(1f), "Reservas Hoy", "0", verdeMedio)
                TarjetaEstadistica(Modifier.weight(1f), "Canchas Ocupadas", "0", verdeOscuro)
            }

            // Sección de lista rápida de próximas reservas
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Próximas Reservas", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = verdeOscuro)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (proximasReservas.isEmpty()) {
                        Text("No hay reservas aún", fontSize = 14.sp, color = Color.Gray)
                    } else {
                        // Muestra solo los elementos de la lista
                        proximasReservas.forEach { reserva ->
                            Text(text = reserva, fontSize = 14.sp, modifier = Modifier.padding(vertical = 5.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
            }

            // Botones principales de navegación
            Button(
                onClick = onNuevaReserva, 
                modifier = Modifier.fillMaxWidth(), 
                colors = ButtonDefaults.buttonColors(containerColor = verdeMedio)
            ) {
                Text("Nueva Reserva")
            }
            
            Button(
                onClick = onListadoReservas, 
                modifier = Modifier.fillMaxWidth(), 
                colors = ButtonDefaults.buttonColors(containerColor = verdeOscuro)
            ) {
                Text("Listado de Reservas")
            }
        }
    }
}

/**
 * Componente visual para mostrar indicadores clave (KPIs).
 */
@Composable
fun TarjetaEstadistica(modifier: Modifier, titulo: String, valor: String, color: Color) {
    Card(
        modifier = modifier.height(90.dp), 
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), 
            verticalArrangement = Arrangement.Center, 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, color = Color.White, fontSize = 12.sp)
            Text(valor, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

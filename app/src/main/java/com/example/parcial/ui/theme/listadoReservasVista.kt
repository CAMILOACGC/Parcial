package com.example.parcial.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Edit

// Clase de datos auxiliar para la vista (Diferente a la del modelo de negocio)
data class Reserva(
    val cliente: String,
    val fecha: String,
    val hora: String,
    val cancha: String,
    val estado: String
)

@Composable
fun ListadoReservasVista(
    modifier: Modifier = Modifier,
    reservas: List<Reserva>,
    onVolver: () -> Unit
) {
    val verdeApp = Color(0xFF388E3C)
    var busqueda by remember { mutableStateOf("") }

    val reservasFiltradas = reservas.filter {
        it.cliente.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(verdeApp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "<",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onVolver() }
            )
            Spacer(modifier = Modifier.width(40.dp))
            Text(
                text = "Listado de Reservas",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("Buscar reserva...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = verdeApp
                    )
                },
                shape = RoundedCornerShape(8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE8F5E9))
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cliente", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                Text("Fecha", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(2f))
                Text("Hora", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                Text("Cancha", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("Estado", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
                Text("Acciones", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f))
            }

            HorizontalDivider()

            if (reservasFiltradas.isEmpty()) {
                Text("No hay reservas", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
            } else {
                LazyColumn {
                    items(reservasFiltradas) { reserva ->
                        FilaReserva(reserva = reserva)
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                    }
                }
            }
        }
    }
}

@Composable
fun FilaReserva(reserva: Reserva) {
    val colorEstado = if (reserva.estado == "Activa") Color(0xFF388E3C) else Color(0xFFD32F2F)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(reserva.cliente, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
        Text(reserva.fecha, fontSize = 12.sp, modifier = Modifier.weight(2f))
        Text(reserva.hora, fontSize = 12.sp, modifier = Modifier.weight(1.5f))
        Text(reserva.cancha, fontSize = 12.sp, modifier = Modifier.weight(1f))

        Box(modifier = Modifier.weight(1.5f)) {
            Text(
                text = reserva.estado,
                color = Color.White,
                fontSize = 11.sp,
                modifier = Modifier
                    .background(colorEstado, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )
        }

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Editar",
            tint = Color(0xFF388E3C),
            modifier = Modifier
                .weight(1f)
                .size(18.dp)
        )
    }
}

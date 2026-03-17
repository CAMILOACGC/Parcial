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
import com.example.parcial.ui.theme.MiPrimeraVista
import com.example.parcial.ui.theme.ParcialTheme

sealed class Pantalla {
    object Dashboard : Pantalla()
    object NuevaReserva : Pantalla()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ParcialTheme {

                var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Dashboard) }
                val proximasReservas = remember { mutableStateListOf<String>() }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (pantallaActual) {
                        is Pantalla.Dashboard -> DashboardVista(
                            modifier = Modifier.padding(innerPadding),
                            proximasReservas = proximasReservas,
                            onNuevaReserva = { pantallaActual = Pantalla.NuevaReserva },
                            onListadoReservas = { /* próximamente */ }
                        )
                        is Pantalla.NuevaReserva -> MiPrimeraVista(
                            modifier = Modifier.padding(innerPadding),
                            onGuardar = { nombre, hora, cancha ->
                                proximasReservas.add("$nombre - $hora - $cancha")
                                pantallaActual = Pantalla.Dashboard
                            },
                            onCancelar = { pantallaActual = Pantalla.Dashboard }
                        )
                    }
                }
            }
        }
    }
}
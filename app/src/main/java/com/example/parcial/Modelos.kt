package com.example.parcial

import kotlin.random.Random

data class Persona(
    var id: Int = Random.nextInt(1, 10000),
    var nombre: String,
    var telefono: String,
    var correo: String = ""
)

data class Cancha(
    var id: Int,
    var nombre: String,
    var tipo: String,
    var estaDisponible: Boolean = true
)

package com.example.parcial

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index
import kotlin.random.Random

@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var nombre: String,
    var telefono: String,
    var correo: String = ""
)

@Entity(tableName = "canchas")
data class Cancha(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var nombre: String,
    var tipo: String,
    var estaDisponible: Boolean = true
)

@Entity(
    tableName = "reservas",
    foreignKeys = [
        ForeignKey(entity = Persona::class, parentColumns = ["id"], childColumns = ["personaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Cancha::class, parentColumns = ["id"], childColumns = ["canchaId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("personaId"), Index("canchaId")]
)
data class Reserva(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var personaId: Int,
    var canchaId: Int,
    var fecha: String,
    var hora: String,
    var estado: String = "Activa"
)

// Clase para obtener la reserva con sus objetos relacionados
data class ReservaConDetalles(
    val id: Int,
    val cliente: Persona,
    val cancha: Cancha,
    val fecha: String,
    val hora: String,
    val estado: String
)

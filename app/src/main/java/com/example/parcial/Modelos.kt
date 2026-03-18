package com.example.parcial

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Entidad que representa a una Persona (Cliente) en la base de datos.
 */
@Entity(tableName = "personas")
data class Persona(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, // Identificador único autoincremental
    var nombre: String,                              // Nombre completo del cliente
    var telefono: String,                            // Número de contacto
    var correo: String = ""                          // Correo electrónico (opcional)
)

/**
 * Entidad que representa una Cancha deportiva.
 */
@Entity(tableName = "canchas")
data class Cancha(
    @PrimaryKey(autoGenerate = true) var id: Int = 0, // Identificador único
    var nombre: String,                              // Nombre o número de la cancha (ej: "Cancha 1")
    var tipo: String,                                // Tipo de superficie o deporte
    var estaDisponible: Boolean = true               // Estado de disponibilidad general
)

/**
 * Entidad que representa una Reserva de una cancha por una persona.
 * Define claves foráneas para mantener la integridad referencial con Personas y Canchas.
 */
@Entity(
    tableName = "reservas",
    foreignKeys = [
        ForeignKey(
            entity = Persona::class, 
            parentColumns = ["id"], 
            childColumns = ["personaId"], 
            onDelete = ForeignKey.CASCADE // Si se elimina la persona, se eliminan sus reservas
        ),
        ForeignKey(
            entity = Cancha::class, 
            parentColumns = ["id"], 
            childColumns = ["canchaId"], 
            onDelete = ForeignKey.CASCADE // Si se elimina la cancha, se eliminan sus reservas
        )
    ],
    indices = [Index("personaId"), Index("canchaId")] // Índices para optimizar las consultas JOIN
)
data class Reserva(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var personaId: Int,       // ID de la persona que reserva
    var canchaId: Int,        // ID de la cancha reservada
    var fecha: String,        // Fecha de la reserva (formato dd/MM/yyyy)
    var hora: String,         // Hora de inicio (formato HH:mm)
    var estado: String = "Activa" // Estado actual de la reserva
)

/**
 * Modelo de datos de dominio que agrupa la información completa de una reserva
 * incluyendo los objetos relacionados (Persona y Cancha).
 * Se utiliza para facilitar el manejo de datos en la interfaz de usuario.
 */
data class ReservaConDetalles(
    val id: Int,
    val cliente: Persona,
    val cancha: Cancha,
    val fecha: String,
    val hora: String,
    val estado: String
)

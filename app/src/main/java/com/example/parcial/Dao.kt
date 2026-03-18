package com.example.parcial

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {
    // Personas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPersona(persona: Persona): Long

    @Update
    suspend fun actualizarPersona(persona: Persona)

    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun obtenerPersonaPorId(id: Int): Persona?

    // Canchas
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCancha(cancha: Cancha)

    @Query("SELECT * FROM canchas")
    fun obtenerCanchas(): Flow<List<Cancha>>

    @Query("SELECT * FROM canchas WHERE nombre = :nombre")
    suspend fun obtenerCanchaPorNombre(nombre: String): Cancha?

    // Reservas
    @Insert
    suspend fun insertarReserva(reserva: Reserva)

    @Update
    suspend fun actualizarReserva(reserva: Reserva)

    @Delete
    suspend fun eliminarReserva(reserva: Reserva)

    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun obtenerReservaPorId(id: Int): Reserva?

    @Query("""
        SELECT r.id, p.id as p_id, p.nombre as p_nombre, p.telefono as p_telefono, p.correo as p_correo,
               c.id as c_id, c.nombre as c_nombre, c.tipo as c_tipo, c.estaDisponible as c_disponible,
               r.fecha, r.hora, r.estado
        FROM reservas r
        JOIN personas p ON r.personaId = p.id
        JOIN canchas c ON r.canchaId = c.id
    """)
    fun obtenerTodasLasReservasConDetalles(): Flow<List<ReservaConDetallesRaw>>
}

// Clase auxiliar para el mapeo manual de la consulta JOIN si no usamos @Relation
data class ReservaConDetallesRaw(
    val id: Int,
    @ColumnInfo(name = "p_id") val pId: Int,
    @ColumnInfo(name = "p_nombre") val pNombre: String,
    @ColumnInfo(name = "p_telefono") val pTelefono: String,
    @ColumnInfo(name = "p_correo") val pCorreo: String,
    @ColumnInfo(name = "c_id") val cId: Int,
    @ColumnInfo(name = "c_nombre") val cNombre: String,
    @ColumnInfo(name = "c_tipo") val cTipo: String,
    @ColumnInfo(name = "c_disponible") val cDisponible: Boolean,
    val fecha: String,
    val hora: String,
    val estado: String
) {
    fun toReservaConDetalles() = ReservaConDetalles(
        id = id,
        cliente = Persona(pId, pNombre, pTelefono, pCorreo),
        cancha = Cancha(cId, cNombre, cTipo, cDisponible),
        fecha = fecha,
        hora = hora,
        estado = estado
    )
}

package com.example.parcial

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Interface DAO (Data Access Object) para definir las operaciones de base de datos.
 * Utiliza Room para interactuar con la base de datos SQLite.
 */
@Dao
interface ReservaDao {
    // --- Operaciones para la tabla Personas ---

    /**
     * Inserta una nueva persona. Si ya existe, la reemplaza.
     * @return El ID de la persona insertada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPersona(persona: Persona): Long

    /**
     * Actualiza la información de una persona existente.
     */
    @Update
    suspend fun actualizarPersona(persona: Persona)

    /**
     * Busca una persona por su ID único.
     */
    @Query("SELECT * FROM personas WHERE id = :id")
    suspend fun obtenerPersonaPorId(id: Int): Persona?

    // --- Operaciones para la tabla Canchas ---

    /**
     * Inserta una nueva cancha.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCancha(cancha: Cancha)

    /**
     * Obtiene todas las canchas registradas como un flujo de datos reactivo (Flow).
     */
    @Query("SELECT * FROM canchas")
    fun obtenerCanchas(): Flow<List<Cancha>>

    /**
     * Busca una cancha específica por su nombre.
     */
    @Query("SELECT * FROM canchas WHERE nombre = :nombre")
    suspend fun obtenerCanchaPorNombre(nombre: String): Cancha?

    // --- Operaciones para la tabla Reservas ---

    /**
     * Registra una nueva reserva en la base de datos.
     */
    @Insert
    suspend fun insertarReserva(reserva: Reserva)

    /**
     * Actualiza los datos de una reserva (ej. cambio de fecha u hora).
     */
    @Update
    suspend fun actualizarReserva(reserva: Reserva)

    /**
     * Elimina una reserva de la base de datos.
     */
    @Delete
    suspend fun eliminarReserva(reserva: Reserva)

    /**
     * Busca una reserva específica por su ID.
     */
    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun obtenerReservaPorId(id: Int): Reserva?

    /**
     * Consulta compleja que une (JOIN) las tablas de reservas, personas y canchas
     * para obtener toda la información detallada de cada reserva.
     * Retorna un Flow para actualizaciones en tiempo real en la UI.
     */
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

/**
 * Clase auxiliar de datos para mapear los resultados de la consulta JOIN.
 * Room no puede mapear directamente a objetos anidados sin @Relation o una clase plana como esta.
 */
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
    /**
     * Convierte el resultado plano de la base de datos a un objeto de dominio con entidades anidadas.
     */
    fun toReservaConDetalles() = ReservaConDetalles(
        id = id,
        cliente = Persona(pId, pNombre, pTelefono, pCorreo),
        cancha = Cancha(cId, cNombre, cTipo, cDisponible),
        fecha = fecha,
        hora = hora,
        estado = estado
    )
}

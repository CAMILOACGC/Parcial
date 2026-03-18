package com.example.parcial

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * AppDatabase: Define la configuración de la base de datos Room.
 * Incluye las entidades que componen la base de datos y la versión.
 */
@Database(entities = [Persona::class, Cancha::class, Reserva::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Define el acceso al DAO para realizar operaciones sobre las tablas.
     */
    abstract fun reservaDao(): ReservaDao

    companion object {
        // La instancia única de la base de datos (Singleton)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * getDatabase: Método para obtener la instancia de la base de datos.
         * Implementa el patrón Singleton para asegurar que solo exista una instancia abierta.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Crea la base de datos usando el constructor de Room
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "reservas_database"
                )
                .fallbackToDestructiveMigration() // Manejo simplificado de cambios en el esquema
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

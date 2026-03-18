# Golf Club - Sistema de Reservas

Este es un proyecto de aplicación Android desarrollado en Kotlin utilizando **Jetpack Compose** para la interfaz de usuario y **Room** para la persistencia de datos local. La aplicación permite gestionar las reservas de canchas de un club de golf.

## Características Principales

- **Dashboard:** Visualización rápida de estadísticas (reservas del día, canchas ocupadas) y un listado de las próximas reservas.
- **Gestión de Reservas:** 
    - Crear nuevas reservas con validación de datos (teléfono, campos obligatorios).
    - Edición de reservas existentes.
    - Eliminación de registros.
- **Validación de Disponibilidad:** El sistema verifica que una cancha no esté ocupada en la misma fecha y hora por otra reserva activa antes de permitir el registro.
- **Base de Datos Local:** Utiliza Room para almacenar información de Clientes (Personas), Canchas y Reservas, manteniendo la integridad referencial.
- **Interfaz Moderna:** Diseño basado en Material Design 3 con soporte para selectores de fecha y hora nativos.

## Estructura del Proyecto

- `MainActivity.kt`: Punto de entrada de la aplicación, gestiona la navegación y la lógica principal de negocio.
- `AppDatabase.kt`: Configuración de la base de datos Room y patrón Singleton.
- `Dao.kt`: Definición de las operaciones de acceso a datos (Queries, Inserts, Updates, Deletes).
- `Modelos.kt`: Definición de las entidades de la base de datos (`Persona`, `Cancha`, `Reserva`).
- `ui/theme/`: Contiene los componentes de interfaz de usuario:
    - `DashboardVista.kt`: Pantalla principal de resumen.
    - `primerVista.kt`: Formulario para creación y edición de reservas.
    - `listadoReservasVista.kt`: Visualización de todas las reservas con filtros de búsqueda.

## Tecnologías Utilizadas

- **Kotlin**
- **Jetpack Compose** (UI)
- **Room Persistence Library** (Base de Datos)
- **Coroutines & Flow** (Asincronía y reactividad de datos)
- **Material 3** (Componentes visuales)

## Requisitos

- Android Studio Ladybug o superior.
- SDK de Android 24 (Android 7.0) como mínimo.

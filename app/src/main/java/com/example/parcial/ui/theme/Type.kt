package com.example.parcial.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Configuración de la tipografía (fuentes) de la aplicación.
 * Define los estilos de texto predeterminados para los diferentes niveles de la jerarquía visual.
 */
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* 
    Se pueden personalizar otros estilos como:
    titleLarge = TextStyle(...),
    labelSmall = TextStyle(...), etc.
    */
)

package com.mindbloom.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.mindbloom.app.R

/**
 * The mock-ups use a geometric rounded sans. Poppins is bundled in res/font so
 * the running app matches the design without any network fetch.
 */
val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold)
)

val MindBloomTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp, lineHeight = 40.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Bold,
        fontSize = 20.sp, lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp, lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 22.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp
    )
)

package com.example.mywallet.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.mywallet.R

@OptIn(ExperimentalTextApi::class)
private fun manropeFont(weight: FontWeight) = Font(
    resId = R.font.manrope_variable,
    weight = weight,
    variationSettings = FontVariation.Settings(
        FontVariation.weight(weight.weight)
    )
)

private val ManropeFontFamily = FontFamily(
    manropeFont(FontWeight.Normal),
    manropeFont(FontWeight.Medium),
    manropeFont(FontWeight.SemiBold),
    manropeFont(FontWeight.Bold)
)

private val BaseTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

val Typography = BaseTypography.copy(
    displayLarge = BaseTypography.displayLarge.copy(fontFamily = ManropeFontFamily),
    displayMedium = BaseTypography.displayMedium.copy(fontFamily = ManropeFontFamily),
    displaySmall = BaseTypography.displaySmall.copy(fontFamily = ManropeFontFamily),
    headlineLarge = BaseTypography.headlineLarge.copy(fontFamily = ManropeFontFamily),
    headlineMedium = BaseTypography.headlineMedium.copy(fontFamily = ManropeFontFamily),
    headlineSmall = BaseTypography.headlineSmall.copy(fontFamily = ManropeFontFamily),
    titleLarge = BaseTypography.titleLarge.copy(fontFamily = ManropeFontFamily),
    titleMedium = BaseTypography.titleMedium.copy(fontFamily = ManropeFontFamily),
    titleSmall = BaseTypography.titleSmall.copy(fontFamily = ManropeFontFamily),
    bodyLarge = BaseTypography.bodyLarge.copy(fontFamily = ManropeFontFamily),
    bodyMedium = BaseTypography.bodyMedium.copy(fontFamily = ManropeFontFamily),
    bodySmall = BaseTypography.bodySmall.copy(fontFamily = ManropeFontFamily),
    labelLarge = BaseTypography.labelLarge.copy(fontFamily = ManropeFontFamily),
    labelMedium = BaseTypography.labelMedium.copy(fontFamily = ManropeFontFamily),
    labelSmall = BaseTypography.labelSmall.copy(fontFamily = ManropeFontFamily)
)

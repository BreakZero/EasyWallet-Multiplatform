package org.easy.wallet.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Theme Colors
val EasyWallet_Light_Primary = Color(0xFF49557F)
val EasyWallet_Light_OnPrimary = Color(0xFFFFFFFF)
val EasyWallet_Light_PrimaryContainer = Color(0xFFDEE0FF)
val EasyWallet_Light_OnPrimaryContainer = Color(0xFF1A2650)
val EasyWallet_Light_Secondary = Color(0xFF745900)
val EasyWallet_Light_OnSecondary = Color(0xFFFFFFFF)
val EasyWallet_Light_Surface = Color(0xFFF8F8FF)
val EasyWallet_Light_OnSurface = Color(0xFF1B1B1F)
val EasyWallet_Light_SecondaryContainer = Color(0xFFE8DEF8)
val EasyWallet_Light_OnSecondaryContainer = Color(0xFF1D192B)
val EasyWallet_Light_Tertiary = Color(0xFF7D5260)
val EasyWallet_Light_Error = Color(0xFFB3261E)
val EasyWallet_Light_Outline = Color(0xFF79747E)

// Dark Theme Colors
val EasyWallet_Dark_Primary = Color(0xFFBEC2FF)
val EasyWallet_Dark_OnPrimary = Color(0xFF1A2650)
val EasyWallet_Dark_PrimaryContainer = Color(0xFF313D67)
val EasyWallet_Dark_OnPrimaryContainer = Color(0xFFDEE0FF)
val EasyWallet_Dark_Secondary = Color(0xFFE8C04A)
val EasyWallet_Dark_OnSecondary = Color(0xFF3D2E00)
val EasyWallet_Dark_Surface = Color(0xFF1B1B1F)
val EasyWallet_Dark_OnSurface = Color(0xFFE4E1E6)
val EasyWallet_Dark_SecondaryContainer = Color(0xFF4A4458)
val EasyWallet_Dark_OnSecondaryContainer = Color(0xFFE8DEF8)
val EasyWallet_Dark_Tertiary = Color(0xFFEFB8C8)
val EasyWallet_Dark_Error = Color(0xFFF2B8B5)
val EasyWallet_Dark_Outline = Color(0xFF938F99)

val EasyWalletLightColorScheme = lightColorScheme(
  primary = EasyWallet_Light_Primary,
  onPrimary = EasyWallet_Light_OnPrimary,
  primaryContainer = EasyWallet_Light_PrimaryContainer,
  onPrimaryContainer = EasyWallet_Light_OnPrimaryContainer,
  secondary = EasyWallet_Light_Secondary,
  onSecondary = EasyWallet_Light_OnSecondary,
  secondaryContainer = EasyWallet_Light_SecondaryContainer,
  onSecondaryContainer = EasyWallet_Light_OnSecondaryContainer,
  tertiary = EasyWallet_Light_Tertiary,
  error = EasyWallet_Light_Error,
  // Note: M3 uses 'surface' and 'background' often for similar things
  surface = EasyWallet_Light_Surface,
  onSurface = EasyWallet_Light_OnSurface,
  outline = EasyWallet_Light_Outline
)

val EasyWalletDarkColorScheme = darkColorScheme(
  primary = EasyWallet_Dark_Primary,
  onPrimary = EasyWallet_Dark_OnPrimary,
  primaryContainer = EasyWallet_Dark_PrimaryContainer,
  onPrimaryContainer = EasyWallet_Dark_OnPrimaryContainer,
  secondary = EasyWallet_Dark_Secondary,
  onSecondary = EasyWallet_Dark_OnSecondary,
  secondaryContainer = EasyWallet_Dark_SecondaryContainer,
  onSecondaryContainer = EasyWallet_Dark_OnSecondaryContainer,
  tertiary = EasyWallet_Dark_Tertiary,
  error = EasyWallet_Dark_Error,
  surface = EasyWallet_Dark_Surface,
  onSurface = EasyWallet_Dark_OnSurface,
  outline = EasyWallet_Dark_Outline
)

@Composable
fun EasyWalletTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colorScheme = when {
    darkTheme -> EasyWalletDarkColorScheme
    else -> EasyWalletLightColorScheme
  }
  MaterialTheme(
    colorScheme = colorScheme,
    content = content
  )
}
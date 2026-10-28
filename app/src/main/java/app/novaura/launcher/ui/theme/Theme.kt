package app.novaura.launcher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val scheme = darkColorScheme(
    primary = NovauraColors.Violet,
    secondary = NovauraColors.NeonPink,
    tertiary = NovauraColors.Teal,
    background = NovauraColors.Background,
    surface = NovauraColors.Panel,
    onPrimary = NovauraColors.TextPrimary,
    onBackground = NovauraColors.TextPrimary,
    onSurface = NovauraColors.TextPrimary,
    error = NovauraColors.Error,
)

@Composable
fun NovauraTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, content = content)

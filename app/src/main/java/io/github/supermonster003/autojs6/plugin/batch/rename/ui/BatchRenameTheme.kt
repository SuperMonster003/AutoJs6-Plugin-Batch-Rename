package io.github.supermonster003.autojs6.plugin.batch.rename.ui

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext

/**
 * Material 3 theme of the rename screen: the host's theme color and night mode when the host shares them, otherwise
 * the system (dynamic colors from Android 12).
 * zh-CN: 重命名界面的 Material 3 主题: 宿主提供主题色与夜间模式时使用之, 否则跟随系统 (Android 12 起使用动态取色).
 */
@Composable
internal fun BatchRenameTheme(appearance: HostAppearance, content: @Composable () -> Unit) {
    val dark = appearance.dark ?: isSystemInDarkTheme()
    val context = LocalContext.current
    val scheme = when {
        appearance.primary != null -> hostScheme(Color(appearance.primary), appearance.accent?.let(::Color), dark)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

/** A scheme around the host's primary color; containers are tints of it over the surface. */
private fun hostScheme(primary: Color, accent: Color?, dark: Boolean): ColorScheme {
    val base = if (dark) darkColorScheme() else lightColorScheme()
    val secondary = accent ?: primary
    val container = primary.copy(alpha = if (dark) 0.32f else 0.18f).compositeOver(base.surface)
    val secondaryContainer = secondary.copy(alpha = if (dark) 0.28f else 0.14f).compositeOver(base.surface)
    return base.copy(
        primary = primary,
        onPrimary = contentColorOn(primary),
        primaryContainer = container,
        onPrimaryContainer = contentColorOn(container),
        secondary = secondary,
        onSecondary = contentColorOn(secondary),
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = contentColorOn(secondaryContainer),
        surfaceTint = primary,
    )
}

private fun contentColorOn(background: Color): Color = if (background.luminance() > 0.45f) Color(0xFF1B1B1F) else Color.White

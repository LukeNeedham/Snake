package com.lukeneedham.snake.ui

import androidx.compose.runtime.Composable

/**
 * While this is in the composition, the icons of the status bar and the navigation bar are light, because the game fades to black
 * under them. The other pages decide for themselves, from the theme.
 */
@Composable
internal expect fun LightSystemBarIcons()

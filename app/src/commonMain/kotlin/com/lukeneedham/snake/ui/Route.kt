package com.lukeneedham.snake.ui

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/** The destinations of the app, one per page: [Game] is `GamePage`, [Settings] is `SettingsPage`, and so on. */
@Serializable
sealed interface Route : NavKey {
    @Serializable
    data object Game : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Debug : Route

    @Serializable
    data object Logs : Route

    @Serializable
    data object Crashes : Route
}

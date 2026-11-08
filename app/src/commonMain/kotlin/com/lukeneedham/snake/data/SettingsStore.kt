package com.lukeneedham.snake.data

import com.lukeneedham.snake.game.SprintSide
import kotlinx.coroutines.flow.Flow

/** Keeps the settings between runs of the app. */
interface SettingsStore {
    /** Which bottom corner of the screen sprints: the left one until the player chooses otherwise. */
    val sprintSide: Flow<SprintSide>

    suspend fun setSprintSide(side: SprintSide)
}

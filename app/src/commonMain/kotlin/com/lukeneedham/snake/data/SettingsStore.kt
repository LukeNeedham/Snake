package com.lukeneedham.snake.data

import com.lukeneedham.snake.game.SprintSide
import kotlinx.coroutines.flow.Flow

/** Keeps the settings between runs of the app. */
interface SettingsStore {
    /** Which bottom corner of the screen sprints: the left one until the player chooses otherwise. */
    val sprintSide: Flow<SprintSide>

    suspend fun setSprintSide(side: SprintSide)

    /** Whether the player has been through the whole tutorial along the path: from then on the path is short, and the tutorial can be done again at the door. */
    val isTutorialDone: Flow<Boolean>

    suspend fun setTutorialDone(isDone: Boolean)
}

package com.lukeneedham.snake.data

import kotlinx.coroutines.flow.Flow

/** Keeps the best score between runs of the app. */
interface BestScoreStore {
    val best: Flow<Int>

    /** Saves [score] if it beats the best one. */
    suspend fun save(score: Int)
}

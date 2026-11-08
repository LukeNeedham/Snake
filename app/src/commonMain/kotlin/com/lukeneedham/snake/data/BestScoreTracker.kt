package com.lukeneedham.snake.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Follows the best score of the [store] and saves the score of a game that has ended, once per game. [onChange] is called
 * when the best score it holds changes from outside.
 */
class BestScoreTracker(
    private val store: BestScoreStore,
    private val scope: CoroutineScope,
    private val onChange: () -> Unit,
) {
    var best = 0
        private set

    /** Whether the game that has just ended beat the best score. */
    var isNewBest = false
        private set

    private var hasSaved = false

    /** Starts following the store. */
    fun start() {
        scope.launch {
            store.best.collect {
                best = it
                onChange()
            }
        }
    }

    fun newGame() {
        hasSaved = false
        isNewBest = false
    }

    /** The game ended with [score]: it is saved if it is the best, and only the first call of a game does anything. */
    fun gameOver(score: Int) {
        if (hasSaved) return
        hasSaved = true
        isNewBest = score > best
        best = max(best, score)
        scope.launch { store.save(score) }
    }
}

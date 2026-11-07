package com.lukeneedham.snake.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.snake.game.Direction
import com.lukeneedham.snake.game.SnakeEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(private val engine: SnakeEngine) : ViewModel() {

    private var game = engine.newGame()
    private var pendingDirection = game.direction
    private var status = GameStatus.Ready
    private var best = 0
    private var loop: Job? = null

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    /** Starts, pauses or resumes, or begins a new game after a loss. */
    fun onTap() {
        when (status) {
            GameStatus.Ready, GameStatus.Paused -> run()
            GameStatus.Running -> pause()
            GameStatus.GameOver -> restart()
        }
    }

    fun onSwipe(direction: Direction) {
        if (status == GameStatus.Ready) run()
        if (status != GameStatus.Running) return
        if (!direction.isOppositeOf(game.direction)) pendingDirection = direction
    }

    /** The app left the screen. */
    fun onPause() {
        if (status == GameStatus.Running) pause()
    }

    /** One move of the snake. Driven by the loop of [run]; `internal` so that tests need no clock. */
    internal fun tick() {
        game = engine.step(game, pendingDirection)
        if (game.isOver) {
            status = GameStatus.GameOver
            best = maxOf(best, game.score)
            loop?.cancel()
        }
        publish()
    }

    private fun run() {
        status = GameStatus.Running
        publish()
        loop?.cancel()
        loop = viewModelScope.launch {
            while (status == GameStatus.Running) {
                delay(tickDelayMillis())
                if (status == GameStatus.Running) tick()
            }
        }
    }

    private fun pause() {
        status = GameStatus.Paused
        loop?.cancel()
        publish()
    }

    private fun restart() {
        game = engine.newGame()
        pendingDirection = game.direction
        run()
    }

    /** The snake gets faster with every piece of food. */
    private fun tickDelayMillis(): Long = (START_DELAY_MILLIS - game.score * SPEED_UP_MILLIS).coerceAtLeast(MIN_DELAY_MILLIS)

    private fun publish() {
        _uiState.value = buildState()
    }

    private fun buildState() = GameUiState(
        width = game.width,
        height = game.height,
        snake = game.snake,
        food = game.food,
        score = game.score,
        best = best,
        status = status,
    )

    private companion object {
        const val START_DELAY_MILLIS = 200L
        const val SPEED_UP_MILLIS = 5L
        const val MIN_DELAY_MILLIS = 80L
    }
}

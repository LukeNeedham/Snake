package com.lukeneedham.snake.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.snake.data.BestScoreStore
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameTuning
import com.lukeneedham.snake.game.GameWorld
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class GameViewModel(private val bestScoreStore: BestScoreStore) : ViewModel() {

    private var world = GameWorld(Arena(GameTuning.WORLD_WIDTH.toFloat(), DEFAULT_HEIGHT))
    private var bestScore = 0
    private var isNewBest = false
    private var savedThisGame = false

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bestScoreStore.best.collect {
                bestScore = it
                publish()
            }
        }
    }

    /** The arena is an oval that fills the screen, so the world takes the aspect ratio of the screen. */
    fun onSceneSize(widthPx: Int, heightPx: Int) {
        if (widthPx <= 0 || heightPx <= 0) return
        val height = (GameTuning.WORLD_WIDTH.toFloat() * heightPx / widthPx).roundToInt()
        if (height == world.arena.height.toInt()) return
        newWorld(height.toFloat())
    }

    /** Called once per frame with the seconds since the last frame. */
    fun onFrame(seconds: Float) {
        world.update(seconds.coerceIn(0f, MAX_STEP))
        if (world.phase == GamePhase.GAME_OVER && !savedThisGame) {
            savedThisGame = true
            isNewBest = world.score > bestScore
            val score = world.score
            viewModelScope.launch { bestScoreStore.save(score) }
        }
        publish()
    }

    fun onTap() {
        when (world.phase) {
            GamePhase.READY -> world.start()
            GamePhase.PLAYING -> world.tap()
            GamePhase.PAUSED -> world.resume()
            GamePhase.GAME_OVER -> if (world.canRestart) {
                newWorld(world.arena.height)
                world.start()
            }
        }
        publish()
    }

    fun onPause() {
        world.pause()
        publish()
    }

    private fun newWorld(height: Float) {
        world = GameWorld(Arena(GameTuning.WORLD_WIDTH.toFloat(), height), seed = (world.time * 1000f).toInt())
        savedThisGame = false
        isNewBest = false
        publish()
    }

    private fun publish() {
        val state = buildState()
        if (state != _uiState.value) _uiState.value = state
    }

    private fun buildState() = GameUiState(
        world = world,
        phase = world.phase,
        score = world.score,
        bestScore = maxOf(bestScore, if (world.phase == GamePhase.GAME_OVER) world.score else 0),
        isNewBest = isNewBest,
    )

    private companion object {
        const val DEFAULT_HEIGHT = 234f
        const val MAX_STEP = 0.05f
    }
}

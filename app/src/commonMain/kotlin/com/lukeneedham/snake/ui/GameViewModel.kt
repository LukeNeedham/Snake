package com.lukeneedham.snake.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lukeneedham.snake.data.BestScoreStore
import com.lukeneedham.snake.data.BestScoreTracker
import com.lukeneedham.snake.game.Arena
import com.lukeneedham.snake.game.GamePhase
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.WorldTuning
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

/** Connects the game to the page: it steps the world every frame, passes the fingers on, and publishes what the page shows. */
class GameViewModel(bestScoreStore: BestScoreStore) : ViewModel() {

    private var world = GameWorld(Arena(WorldTuning.width.toFloat(), defaultHeight))
    private val bestScores = BestScoreTracker(bestScoreStore, viewModelScope) { publish() }

    private val _uiState = MutableStateFlow(buildState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        bestScores.start()
    }

    /** The arena is an oval that fills the screen, so the world takes the aspect ratio of the screen. */
    fun onSceneSize(widthPx: Int, heightPx: Int) {
        if (widthPx <= 0 || heightPx <= 0) return
        val height = (WorldTuning.width.toFloat() * heightPx / widthPx).roundToInt()
        if (height == world.arena.height.toInt()) return
        newWorld(height.toFloat())
    }

    /** Called once per frame with the seconds since the last frame. */
    fun onFrame(seconds: Float) {
        world.update(seconds.coerceIn(0f, maxStep))
        if (world.phase == GamePhase.GameOver) bestScores.gameOver(world.score)
        publish()
    }

    /**
     * A finger went down. While playing, what it does depends on how long it stays down (a short press turns the snake, a hold
     * sprints), so that waits for [onRelease]; in the other phases it acts at once.
     */
    fun onPress(pointerId: Long) {
        when (world.phase) {
            GamePhase.Ready -> world.start()
            GamePhase.Playing -> world.press(pointerId)
            GamePhase.Paused -> world.resume()
            GamePhase.GameOver -> if (world.canRestart) {
                newWorld(world.arena.height)
                world.start()
            }
        }
        publish()
    }

    fun onRelease(pointerId: Long) {
        world.release(pointerId)
        publish()
    }

    fun onPause() {
        world.pause()
        publish()
    }

    private fun newWorld(height: Float) {
        world = GameWorld(Arena(WorldTuning.width.toFloat(), height), seed = (world.time * 1000f).toInt())
        bestScores.newGame()
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
        bestScore = bestScores.best,
        isNewBest = bestScores.isNewBest,
    )

    private companion object {
        const val defaultHeight = 234f
        const val maxStep = 0.05f
    }
}

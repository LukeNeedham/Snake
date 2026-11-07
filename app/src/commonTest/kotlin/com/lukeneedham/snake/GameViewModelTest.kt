package com.lukeneedham.snake

import com.lukeneedham.snake.game.Direction
import com.lukeneedham.snake.game.SnakeEngine
import com.lukeneedham.snake.ui.GameStatus
import com.lukeneedham.snake.ui.GameViewModel
import kotlin.random.Random
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private lateinit var viewModel: GameViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = GameViewModel(SnakeEngine(width = 15, height = 20, random = Random(1)))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun theGameWaitsForAFirstInput() {
        assertEquals(GameStatus.Ready, viewModel.uiState.value.status)
    }

    @Test
    fun aSwipeStartsTheGameAndSteers() {
        val headBefore = viewModel.uiState.value.snake.first()
        viewModel.onSwipe(Direction.Up)
        assertEquals(GameStatus.Running, viewModel.uiState.value.status)
        viewModel.tick()
        val head = viewModel.uiState.value.snake.first()
        assertEquals(headBefore.x, head.x)
        assertEquals(headBefore.y - 1, head.y)
    }

    @Test
    fun tappingPausesAndResumes() {
        viewModel.onTap()
        assertEquals(GameStatus.Running, viewModel.uiState.value.status)
        viewModel.onTap()
        assertEquals(GameStatus.Paused, viewModel.uiState.value.status)
        viewModel.onTap()
        assertEquals(GameStatus.Running, viewModel.uiState.value.status)
    }

    @Test
    fun leavingTheScreenPausesARunningGame() {
        viewModel.onTap()
        viewModel.onPause()
        assertEquals(GameStatus.Paused, viewModel.uiState.value.status)
    }

    @Test
    fun hittingAWallEndsTheGameAndATapStartsANewOne() {
        viewModel.onTap()
        repeat(20) { viewModel.tick() }
        assertEquals(GameStatus.GameOver, viewModel.uiState.value.status)
        viewModel.onTap()
        assertEquals(GameStatus.Running, viewModel.uiState.value.status)
        assertEquals(0, viewModel.uiState.value.score)
    }
}

package com.lukeneedham.snake

import com.lukeneedham.snake.data.SettingsStore
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.ui.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

// Names without spaces: a test of commonTest also runs on the targets that do not allow them
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private class FakeSettingsStore : SettingsStore {
        val stored = MutableStateFlow(SprintSide.Left)
        override val sprintSide: Flow<SprintSide> = stored

        override suspend fun setSprintSide(side: SprintSide) {
            stored.value = side
        }

        val tutorialDone = MutableStateFlow(false)
        override val isTutorialDone: Flow<Boolean> = tutorialDone

        override suspend fun setTutorialDone(isDone: Boolean) {
            tutorialDone.value = isDone
        }
    }

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    @Test
    fun theStateSaysWhetherDebuggingIsOn() {
        assertEquals(true, SettingsViewModel(isDebugEnabled = true, settingsStore = FakeSettingsStore()).uiState.value.isDebugEnabled)
        assertEquals(false, SettingsViewModel(isDebugEnabled = false, settingsStore = FakeSettingsStore()).uiState.value.isDebugEnabled)
    }

    @Test
    fun theSprintButtonIsOnTheLeftByDefault() = runTest(dispatcher) {
        val viewModel = SettingsViewModel(isDebugEnabled = false, settingsStore = FakeSettingsStore())
        runCurrent()
        assertEquals(SprintSide.Left, viewModel.uiState.value.sprintSide)
    }

    @Test
    fun choosingASideSavesItAndShowsIt() = runTest(dispatcher) {
        val store = FakeSettingsStore()
        val viewModel = SettingsViewModel(isDebugEnabled = false, settingsStore = store)
        viewModel.onSprintSide(SprintSide.Right)
        runCurrent()
        assertEquals(SprintSide.Right, store.stored.value)
        assertEquals(SprintSide.Right, viewModel.uiState.value.sprintSide)
    }
}

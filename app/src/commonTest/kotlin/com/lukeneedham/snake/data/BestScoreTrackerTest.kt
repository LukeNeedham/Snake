package com.lukeneedham.snake.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BestScoreTrackerTest {

    private class FakeBestScoreStore(initial: Int) : BestScoreStore {
        val stored = MutableStateFlow(initial)
        override val best: Flow<Int> = stored

        override suspend fun save(score: Int) {
            if (score > stored.value) stored.value = score
        }
    }

    @Test
    fun itFollowsTheStoredBestScore() = runTest {
        var changes = 0
        val tracker = BestScoreTracker(FakeBestScoreStore(5), backgroundScope) { changes++ }
        tracker.start()
        runCurrent()
        assertEquals(5, tracker.best)
        assertTrue(changes > 0)
    }

    @Test
    fun aScoreAboveTheBestIsANewBestAndIsSaved() = runTest {
        val store = FakeBestScoreStore(5)
        val tracker = BestScoreTracker(store, backgroundScope) {}
        tracker.start()
        runCurrent()
        tracker.gameOver(9)
        runCurrent()
        assertTrue(tracker.isNewBest)
        assertEquals(9, tracker.best)
        assertEquals(9, store.stored.value)
    }

    @Test
    fun aScoreBelowTheBestIsNotANewBest() = runTest {
        val store = FakeBestScoreStore(5)
        val tracker = BestScoreTracker(store, backgroundScope) {}
        tracker.start()
        runCurrent()
        tracker.gameOver(3)
        runCurrent()
        assertFalse(tracker.isNewBest)
        assertEquals(5, tracker.best)
        assertEquals(5, store.stored.value)
    }

    @Test
    fun onlyTheFirstCallOfAGameCounts() = runTest {
        val store = FakeBestScoreStore(0)
        val tracker = BestScoreTracker(store, backgroundScope) {}
        tracker.start()
        runCurrent()
        tracker.gameOver(4)
        tracker.gameOver(20)
        runCurrent()
        assertEquals(4, store.stored.value)
        tracker.newGame()
        assertFalse(tracker.isNewBest)
        tracker.gameOver(20)
        runCurrent()
        assertEquals(20, store.stored.value)
    }
}

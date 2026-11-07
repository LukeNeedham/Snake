package com.lukeneedham.snake

import com.lukeneedham.snake.game.Cell
import com.lukeneedham.snake.game.Direction
import com.lukeneedham.snake.game.SnakeEngine
import com.lukeneedham.snake.game.SnakeGame
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SnakeEngineTest {

    private val engine = SnakeEngine(width = 10, height = 10, random = Random(1))

    private fun game(
        snake: List<Cell>,
        direction: Direction = Direction.Right,
        food: Cell? = Cell(0, 0),
    ) = SnakeGame(width = 10, height = 10, snake = snake, direction = direction, food = food, score = 0, isOver = false)

    @Test
    fun aNewGameHasASnakeOfThreeAndFoodOffTheSnake() {
        val game = engine.newGame()
        assertEquals(3, game.snake.size)
        assertNotNull(game.food)
        assertFalse(game.food in game.snake)
        assertFalse(game.isOver)
    }

    @Test
    fun theSnakeMovesOneCellAndKeepsItsLength() {
        val next = engine.step(game(listOf(Cell(5, 5), Cell(4, 5), Cell(3, 5))), Direction.Right)
        assertEquals(listOf(Cell(6, 5), Cell(5, 5), Cell(4, 5)), next.snake)
    }

    @Test
    fun turningBackOnItselfIsIgnored() {
        val next = engine.step(game(listOf(Cell(5, 5), Cell(4, 5), Cell(3, 5))), Direction.Left)
        assertEquals(Cell(6, 5), next.snake.first())
        assertEquals(Direction.Right, next.direction)
    }

    @Test
    fun eatingGrowsTheSnakeAndScores() {
        val start = game(listOf(Cell(5, 5), Cell(4, 5), Cell(3, 5)), food = Cell(6, 5))
        val next = engine.step(start, Direction.Right)
        assertEquals(4, next.snake.size)
        assertEquals(1, next.score)
        assertFalse(next.food in next.snake)
        assertFalse(next.isOver)
    }

    @Test
    fun hittingAWallEndsTheGame() {
        val next = engine.step(game(listOf(Cell(9, 5), Cell(8, 5), Cell(7, 5))), Direction.Right)
        assertTrue(next.isOver)
    }

    @Test
    fun hittingItselfEndsTheGame() {
        // A tight loop: moving down from (4,4) runs into (4,5), which is part of the body
        val snake = listOf(Cell(4, 4), Cell(5, 4), Cell(5, 5), Cell(4, 5), Cell(3, 5))
        val next = engine.step(game(snake, direction = Direction.Left), Direction.Down)
        assertTrue(next.isOver)
    }

    @Test
    fun theTailCellIsFreeToMoveInto() {
        // The tail at (4,5) moves away in the same step, so the head may take its place
        val snake = listOf(Cell(4, 4), Cell(5, 4), Cell(5, 5), Cell(4, 5))
        val next = engine.step(game(snake, direction = Direction.Left), Direction.Down)
        assertFalse(next.isOver)
    }

    @Test
    fun aFinishedGameDoesNotMove() {
        val over = game(listOf(Cell(5, 5), Cell(4, 5))).copy(isOver = true)
        assertEquals(over, engine.step(over, Direction.Up))
    }

    @Test
    fun fillingTheBoardEndsTheGameWithNoFood() {
        val small = SnakeEngine(width = 2, height = 1, random = Random(1))
        val start = SnakeGame(2, 1, listOf(Cell(0, 0)), Direction.Right, Cell(1, 0), 0, false)
        val next = small.step(start, Direction.Right)
        assertNull(next.food)
        assertTrue(next.isOver)
    }
}

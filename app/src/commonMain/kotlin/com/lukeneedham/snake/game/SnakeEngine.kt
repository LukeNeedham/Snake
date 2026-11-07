package com.lukeneedham.snake.game

import kotlin.random.Random

/** The rules of the game, with no UI and no clock: [step] turns one snapshot into the next. */
class SnakeEngine(
    private val width: Int,
    private val height: Int,
    private val random: Random,
) {
    fun newGame(): SnakeGame {
        val head = Cell(width / 2, height / 2)
        val snake = listOf(head, Cell(head.x - 1, head.y), Cell(head.x - 2, head.y))
        return SnakeGame(
            width = width,
            height = height,
            snake = snake,
            direction = Direction.Right,
            food = randomFood(snake),
            score = 0,
            isOver = false,
        )
    }

    /** Moves the snake one cell towards [requested], or keeps its direction if that would turn it back on itself. */
    fun step(game: SnakeGame, requested: Direction): SnakeGame {
        if (game.isOver) return game
        val direction = if (requested.isOppositeOf(game.direction)) game.direction else requested
        val oldHead = game.snake.first()
        val head = Cell(oldHead.x + direction.dx, oldHead.y + direction.dy)

        val hitsWall = head.x !in 0 until width || head.y !in 0 until height
        if (hitsWall) return game.copy(direction = direction, isOver = true)

        val eats = head == game.food
        // The tail moves out of the way unless the snake grows
        val body = if (eats) game.snake else game.snake.dropLast(1)
        if (head in body) return game.copy(direction = direction, isOver = true)

        val snake = listOf(head) + body
        if (!eats) return game.copy(snake = snake, direction = direction)

        val food = randomFood(snake)
        return game.copy(
            snake = snake,
            direction = direction,
            food = food,
            score = game.score + 1,
            // No free cell left: the board is full, which is a win
            isOver = food == null,
        )
    }

    private fun randomFood(snake: List<Cell>): Cell? {
        val taken = snake.toSet()
        val free = buildList {
            for (y in 0 until height) for (x in 0 until width) {
                val cell = Cell(x, y)
                if (cell !in taken) add(cell)
            }
        }
        return if (free.isEmpty()) null else free[random.nextInt(free.size)]
    }
}

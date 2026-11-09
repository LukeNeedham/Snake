package com.lukeneedham.snake.game

/**
 * How one kind of animal comes out of the grass in a level: the first one after [firstDelay] seconds, the next after a random time between
 * [delayMin] and [delayMax], one at a time at the start and one more for every [pointsPerExtra] points, but never more than [most].
 */
class CritterRate(
    val firstDelay: Float,
    val delayMin: Float,
    val delayMax: Float,
    val pointsPerExtra: Int,
    val most: Int,
)

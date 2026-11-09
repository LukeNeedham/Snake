package com.lukeneedham.snake.game

/** The points each animal is worth in a level. */
class ScoreRules(
    val mouse: Int = ScoreTuning.mouse,
    val toad: Int = ScoreTuning.toad,
    val bird: Int = ScoreTuning.bird,
) {
    companion object {
        val standard = ScoreRules()
    }
}

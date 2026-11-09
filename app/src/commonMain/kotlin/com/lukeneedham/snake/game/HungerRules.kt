package com.lukeneedham.snake.game

/** How hungry the snake gets in a level, and what fills it up. The bar is 1 when full and 0 when empty. */
class HungerRules(
    /** How much of the bar is lost every second. */
    val drainPerSecond: Float = HungerTuning.drainPerSecond,
    /** While sprinting at full strength the bar drains this many times as fast. */
    val sprintDrainFactor: Float = HungerTuning.sprintDrainFactor,
    val mouseFills: Float = HungerTuning.mouseFills,
    val birdFills: Float = HungerTuning.birdFills,
    val toadFills: Float = HungerTuning.toadFills,
    /** Below this the bar is low, and warns. */
    val lowLevel: Float = HungerTuning.lowLevel,
) {
    companion object {
        val standard = HungerRules()
    }
}

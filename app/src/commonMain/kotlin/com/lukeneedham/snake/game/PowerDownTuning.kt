package com.lukeneedham.snake.game

/** The numbers of the power downs of the toads. */
object PowerDownTuning {
    /** The lazy snake goes this many times as fast, however it goes. */
    const val lazySpeedFactor = 0.55f

    /** A greedy snake gets this fraction of the food it usually gets. */
    const val greedyFeedFactor = 0.5f

    /** The hunger bar of a ravenous snake drains this many times as fast. */
    const val ravenousDrainFactor = 2f
}

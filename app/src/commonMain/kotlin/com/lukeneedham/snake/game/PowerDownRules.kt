package com.lukeneedham.snake.game

/** What the power downs of the toads do in a level, and how long each one lasts. */
class PowerDownRules(
    /** The lazy snake goes this many times as fast, however it goes. */
    val lazySpeedFactor: Float = PowerDownTuning.lazySpeedFactor,
    /** A greedy snake gets this fraction of the food it usually gets. */
    val greedyFeedFactor: Float = PowerDownTuning.greedyFeedFactor,
    /** The hunger bar of a ravenous snake drains this many times as fast. */
    val ravenousDrainFactor: Float = PowerDownTuning.ravenousDrainFactor,
    /** How long each power down lasts, in seconds; one that is not here lasts as long as it usually does. */
    val durations: Map<PowerDown, Float> = emptyMap(),
) {
    fun duration(powerDown: PowerDown): Float = durations[powerDown] ?: powerDown.durationSeconds

    companion object {
        val standard = PowerDownRules()
    }
}

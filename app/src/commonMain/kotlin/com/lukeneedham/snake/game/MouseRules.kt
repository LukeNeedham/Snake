package com.lukeneedham.snake.game

/** How the mice of a level come and run: how often, how fast, how soon they notice the snake, and how they act around its cheese. */
class MouseRules(
    val spawn: CritterRate = CritterRate(
        firstDelay = SpawnTuning.firstMouseDelay,
        delayMin = SpawnTuning.mouseDelayMin,
        delayMax = SpawnTuning.mouseDelayMax,
        pointsPerExtra = SpawnTuning.pointsPerExtraMouse,
        most = SpawnTuning.mostMice,
    ),
    /** How fast the mice run, as a part of their normal speed. */
    val speedFactor: Float = 1f,
    val fleeSpeed: Float = CritterTuning.fleeSpeed,
    val fleeSeconds: Float = CritterTuning.fleeSeconds,
    /** A mouse sees the snake in front of it from this far away. */
    val noticeDistance: Float = CritterTuning.noticeDistance,
    val cheeseSmellDistance: Float = CritterTuning.cheeseSmellDistance,
    val cheeseSpeed: Float = CritterTuning.cheeseSpeed,
    val cheeseCrowdDistance: Float = CritterTuning.cheeseCrowdDistance,
    val cheeseDazeSeconds: Float = CritterTuning.cheeseDazeSeconds,
) {
    companion object {
        val standard = MouseRules()
    }
}

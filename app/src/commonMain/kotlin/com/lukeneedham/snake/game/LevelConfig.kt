package com.lukeneedham.snake.game

/**
 * What makes one level a challenge of its own. A stage is given a level, and everything that a level could change is here, in groups of rules
 * that each have one job: a new level is a new value of this and nothing else, and what it does not say is the way it usually is.
 */
class LevelConfig(
    val stageShape: StageShape = StageShape.full,
    val snake: SnakeRules = SnakeRules.standard,
    val hunger: HungerRules = HungerRules.standard,
    val score: ScoreRules = ScoreRules.standard,
    val mice: MouseRules = MouseRules.standard,
    val toads: ToadRules = ToadRules.standard,
    val birds: BirdRules = BirdRules.standard,
    val powerUps: PowerUpRules = PowerUpRules.standard,
    val powerDowns: PowerDownRules = PowerDownRules.standard,
) {
    companion object {
        /** The first level, which is also the way the game is played when nothing else is said. */
        val first = LevelConfig()
    }
}

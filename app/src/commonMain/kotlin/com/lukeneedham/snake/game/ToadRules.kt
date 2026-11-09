package com.lukeneedham.snake.game

/** How the toads of a level come and hop: how often, which kinds (and so which poisons), and how long a hop takes. */
class ToadRules(
    val spawn: CritterRate = CritterRate(
        firstDelay = SpawnTuning.firstToadDelay,
        delayMin = SpawnTuning.toadDelayMin,
        delayMax = SpawnTuning.toadDelayMax,
        pointsPerExtra = SpawnTuning.pointsPerExtraToad,
        most = SpawnTuning.mostToads,
    ),
    val kinds: List<ToadKind> = ToadKind.entries,
    val hopDuration: Float = CritterTuning.hopDuration,
) {
    companion object {
        val standard = ToadRules()
    }
}

package com.lukeneedham.snake.render

import androidx.compose.runtime.Immutable

/** The words drawn into the scene with the pixel font, already translated. */
@Immutable
internal data class SceneTexts(
    val title: String,
    val tapToStart: String,

    /** The words of the story and of the lessons at the start: the snake sleeps until it is woken, then the lessons, one after the other. */
    val tapToWake: String,
    val tapToTurn: String,
    val bellyDecreases: String,
    val holdToZoom: String,
    val zoomingMakesHungry: String,
    val eatToNotStarve: String,
    val youCanEatToads: String,
    val butTheyPoison: String,
    val tryToCatchBirds: String,
    val theyGivePowers: String,
    val score: String,
    val best: String,
    val newBest: String,
    val tapToRetry: String,
    val paused: String,
    val menuTitle: String,
    val menuResume: String,
    val menuBack: String,
    val menuRestart: String,
    val menuSettings: String,
    val statusTitle: String,
    val statusEmpty: String,
    val hungerTitle: String,
    val hungerFills: String,
    /** What each effect does, in the order of the `Effect` entries; only the letters, digits and a few marks of the pixel font. */
    val effectDescriptions: List<String>,
    /** What each power up does, in the order of the `PowerUp` entries. */
    val powerUpDescriptions: List<String>,
    /** What the snake died of, in the order of the `DeathCause` entries. */
    val deathCauseNames: List<String>,
    /** The name of each effect, in the order of the `Effect` entries. */
    val effectNames: List<String>,
    /** The name of each power up, in the order of the `PowerUp` entries. */
    val powerUpNames: List<String>,
)

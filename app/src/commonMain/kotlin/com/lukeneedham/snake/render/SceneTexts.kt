package com.lukeneedham.snake.render

import androidx.compose.runtime.Immutable

/** The words drawn into the scene with the pixel font, already translated. */
@Immutable
internal data class SceneTexts(
    val title: String,
    val tapToStart: String,

    /** The words of the story at the start: the snake sleeps until it is woken, then the lessons of the turn and of the sprint. */
    val tapToWake: String,
    val tapToTurn: String,
    val holdToZoom: String,
    val gameOver: String,
    val score: String,
    val best: String,
    val newBest: String,
    val tapToRetry: String,
    val paused: String,
    val menuResume: String,
    val menuBack: String,
    val menuRestart: String,
    val menuSettings: String,
    /** The name of each effect, in the order of the `Effect` entries. */
    val effectNames: List<String>,
    /** The name of each power up, in the order of the `PowerUp` entries. */
    val powerUpNames: List<String>,
)

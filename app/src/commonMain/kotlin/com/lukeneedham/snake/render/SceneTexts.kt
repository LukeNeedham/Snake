package com.lukeneedham.snake.render

import androidx.compose.runtime.Immutable

/** The words drawn into the scene with the pixel font, already translated. */
@Immutable
internal data class SceneTexts(
    val title: String,
    val tapToStart: String,
    val gameOver: String,
    val score: String,
    val best: String,
    val newBest: String,
    val tapToRetry: String,
    val paused: String,
    val tapToResume: String,
    /** The name of each effect, in the order of the `Effect` entries. */
    val effectNames: List<String>,
)

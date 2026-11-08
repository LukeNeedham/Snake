package com.lukeneedham.snake.ui

import androidx.compose.runtime.Composable
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.generated.resources.Res
import com.lukeneedham.snake.generated.resources.best_value
import com.lukeneedham.snake.generated.resources.effect_confused
import com.lukeneedham.snake.generated.resources.effect_disguised
import com.lukeneedham.snake.generated.resources.effect_hallucinating
import com.lukeneedham.snake.generated.resources.game_over
import com.lukeneedham.snake.generated.resources.game_title
import com.lukeneedham.snake.generated.resources.menu_back
import com.lukeneedham.snake.generated.resources.menu_restart
import com.lukeneedham.snake.generated.resources.menu_resume
import com.lukeneedham.snake.generated.resources.menu_settings
import com.lukeneedham.snake.generated.resources.new_best
import com.lukeneedham.snake.generated.resources.paused
import com.lukeneedham.snake.generated.resources.score_value
import com.lukeneedham.snake.generated.resources.tap_to_retry
import com.lukeneedham.snake.generated.resources.tap_to_start
import com.lukeneedham.snake.render.SceneTexts
import org.jetbrains.compose.resources.stringResource

/** Reads the translated words that the scene draws with its pixel font. */
@Composable
internal fun sceneTexts(score: Int, bestScore: Int, isNewBest: Boolean): SceneTexts = SceneTexts(
    title = stringResource(Res.string.game_title),
    tapToStart = stringResource(Res.string.tap_to_start),
    gameOver = stringResource(Res.string.game_over),
    score = stringResource(Res.string.score_value, score),
    best = stringResource(Res.string.best_value, bestScore),
    newBest = if (isNewBest) stringResource(Res.string.new_best) else "",
    tapToRetry = stringResource(Res.string.tap_to_retry),
    paused = stringResource(Res.string.paused),
    menuResume = stringResource(Res.string.menu_resume),
    menuBack = stringResource(Res.string.menu_back),
    menuRestart = stringResource(Res.string.menu_restart),
    menuSettings = stringResource(Res.string.menu_settings),
    effectNames = Effect.entries.map { effect ->
        when (effect) {
            Effect.Hallucinating -> stringResource(Res.string.effect_hallucinating)
            Effect.Disguised -> stringResource(Res.string.effect_disguised)
            Effect.Confused -> stringResource(Res.string.effect_confused)
        }
    },
)

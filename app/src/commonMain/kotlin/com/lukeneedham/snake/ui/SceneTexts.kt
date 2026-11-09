package com.lukeneedham.snake.ui

import androidx.compose.runtime.Composable
import com.lukeneedham.snake.game.Effect
import com.lukeneedham.snake.game.PowerUp
import com.lukeneedham.snake.generated.resources.Res
import com.lukeneedham.snake.generated.resources.best_value
import com.lukeneedham.snake.generated.resources.effect_cannibal
import com.lukeneedham.snake.generated.resources.effect_cannibal_description
import com.lukeneedham.snake.generated.resources.effect_confused
import com.lukeneedham.snake.generated.resources.effect_confused_description
import com.lukeneedham.snake.generated.resources.effect_greedy
import com.lukeneedham.snake.generated.resources.effect_greedy_description
import com.lukeneedham.snake.generated.resources.effect_hallucinating
import com.lukeneedham.snake.generated.resources.effect_hallucinating_description
import com.lukeneedham.snake.generated.resources.effect_hayfever
import com.lukeneedham.snake.generated.resources.effect_hayfever_description
import com.lukeneedham.snake.generated.resources.effect_imposters
import com.lukeneedham.snake.generated.resources.effect_imposters_description
import com.lukeneedham.snake.generated.resources.effect_lazy
import com.lukeneedham.snake.generated.resources.effect_lazy_description
import com.lukeneedham.snake.generated.resources.effect_ravenous
import com.lukeneedham.snake.generated.resources.effect_ravenous_description
import com.lukeneedham.snake.generated.resources.game_over
import com.lukeneedham.snake.generated.resources.game_title
import com.lukeneedham.snake.generated.resources.hunger_fills
import com.lukeneedham.snake.generated.resources.hunger_title
import com.lukeneedham.snake.generated.resources.menu_back
import com.lukeneedham.snake.generated.resources.menu_restart
import com.lukeneedham.snake.generated.resources.menu_resume
import com.lukeneedham.snake.generated.resources.menu_title
import com.lukeneedham.snake.generated.resources.menu_settings
import com.lukeneedham.snake.generated.resources.new_best
import com.lukeneedham.snake.generated.resources.paused
import com.lukeneedham.snake.generated.resources.power_up_bigger_mouth
import com.lukeneedham.snake.generated.resources.power_up_bigger_mouth_description
import com.lukeneedham.snake.generated.resources.power_up_cheese
import com.lukeneedham.snake.generated.resources.power_up_cheese_description
import com.lukeneedham.snake.generated.resources.power_up_fast_sprint
import com.lukeneedham.snake.generated.resources.power_up_fast_sprint_description
import com.lukeneedham.snake.generated.resources.power_up_sneaky
import com.lukeneedham.snake.generated.resources.power_up_sneaky_description
import com.lukeneedham.snake.generated.resources.power_up_toad_free
import com.lukeneedham.snake.generated.resources.power_up_toad_free_description
import com.lukeneedham.snake.generated.resources.power_up_zoom
import com.lukeneedham.snake.generated.resources.power_up_zoom_description
import com.lukeneedham.snake.generated.resources.score_value
import com.lukeneedham.snake.generated.resources.status_empty
import com.lukeneedham.snake.generated.resources.status_title
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
    menuTitle = stringResource(Res.string.menu_title),
    menuResume = stringResource(Res.string.menu_resume),
    menuBack = stringResource(Res.string.menu_back),
    menuRestart = stringResource(Res.string.menu_restart),
    menuSettings = stringResource(Res.string.menu_settings),
    statusTitle = stringResource(Res.string.status_title),
    statusEmpty = stringResource(Res.string.status_empty),
    hungerTitle = stringResource(Res.string.hunger_title),
    hungerFills = stringResource(Res.string.hunger_fills),
    effectDescriptions = Effect.entries.map { effect ->
        when (effect) {
            Effect.Hallucinating -> stringResource(Res.string.effect_hallucinating_description)
            Effect.Imposters -> stringResource(Res.string.effect_imposters_description)
            Effect.Confused -> stringResource(Res.string.effect_confused_description)
            Effect.Hayfever -> stringResource(Res.string.effect_hayfever_description)
            Effect.Greedy -> stringResource(Res.string.effect_greedy_description)
            Effect.Ravenous -> stringResource(Res.string.effect_ravenous_description)
            Effect.Lazy -> stringResource(Res.string.effect_lazy_description)
            Effect.Cannibal -> stringResource(Res.string.effect_cannibal_description)
        }
    },
    powerUpDescriptions = PowerUp.entries.map { powerUp ->
        when (powerUp) {
            PowerUp.Zoom -> stringResource(Res.string.power_up_zoom_description)
            PowerUp.FastSprint -> stringResource(Res.string.power_up_fast_sprint_description)
            PowerUp.Sneaky -> stringResource(Res.string.power_up_sneaky_description)
            PowerUp.BiggerMouth -> stringResource(Res.string.power_up_bigger_mouth_description)
            PowerUp.ToadFree -> stringResource(Res.string.power_up_toad_free_description)
            PowerUp.Cheese -> stringResource(Res.string.power_up_cheese_description)
        }
    },
    effectNames = Effect.entries.map { effect ->
        when (effect) {
            Effect.Hallucinating -> stringResource(Res.string.effect_hallucinating)
            Effect.Imposters -> stringResource(Res.string.effect_imposters)
            Effect.Confused -> stringResource(Res.string.effect_confused)
            Effect.Hayfever -> stringResource(Res.string.effect_hayfever)
            Effect.Greedy -> stringResource(Res.string.effect_greedy)
            Effect.Ravenous -> stringResource(Res.string.effect_ravenous)
            Effect.Lazy -> stringResource(Res.string.effect_lazy)
            Effect.Cannibal -> stringResource(Res.string.effect_cannibal)
        }
    },
    powerUpNames = PowerUp.entries.map { powerUp ->
        when (powerUp) {
            PowerUp.Zoom -> stringResource(Res.string.power_up_zoom)
            PowerUp.FastSprint -> stringResource(Res.string.power_up_fast_sprint)
            PowerUp.Sneaky -> stringResource(Res.string.power_up_sneaky)
            PowerUp.BiggerMouth -> stringResource(Res.string.power_up_bigger_mouth)
            PowerUp.ToadFree -> stringResource(Res.string.power_up_toad_free)
            PowerUp.Cheese -> stringResource(Res.string.power_up_cheese)
        }
    },
)

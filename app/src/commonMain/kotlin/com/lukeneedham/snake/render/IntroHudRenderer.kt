package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.IntroStage
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.game.SprintZone
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * What is on the screen during the story and the lessons at the start. Nothing while the story plays by itself; the lesson of the turn once
 * the bubble has popped; in the room of the tutorial the hunger bar, with an arrow and the words that
 * say that the belly decreases, then the sprint button, with an arrow and the lesson of the sprint, and the words that zooming makes the snake
 * hungry, then the words of the lessons of the mouse, the toad and the bird; the arrows that show the way (to the left, back to the house, up to the stage) are in the world, on the paths. The bars and the banners of the effects and the power ups are there once the animals come.
 */
internal fun DrawScope.drawIntroHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val intro = world.intro ?: return
    if (world.showsHunger) drawStomach(world, topInset)
    if (intro.showsSprintButton) drawSprintButton(world)
    if (intro.stage.isLesson) {
        drawEffectBars(world)
        drawEffectBanner(world, texts)
        drawPowerUpBanner(world, texts)
    }
    when (intro.stage) {
        IntroStage.Teaching -> {
            // Where the big bubble was, on the screen
            val top = (world.layout.houseY - world.cameraY).roundToInt() - lessonAboveHouse
            val bounce = (sin(world.time * 4f) * 1.2f).roundToInt()
            drawCenteredPixelText(texts.tapToTurn, world.arena.width.toInt() / 2, top + bounce, HudPalette.gold, scale = 2)
        }
        IntroStage.Hunger -> drawHungerLesson(world, texts.bellyDecreases, topInset)
        IntroStage.Sprint -> drawZoomLesson(world, texts)
        IntroStage.Zoomed -> drawLessonText(world, texts.zoomingMakesHungry, topInset)
        IntroStage.Mouse -> drawHungerLesson(world, texts.eatToNotStarve, topInset)
        IntroStage.Toad -> drawLessonText(world, texts.youCanEatToads, topInset)
        IntroStage.Dizzy -> drawLessonText(world, texts.butTheyPoison, topInset)
        IntroStage.Bird -> drawLessonText(world, texts.tryToCatchBirds, topInset)
        IntroStage.Powers -> drawLessonText(world, texts.theyGivePowers, topInset)
        else -> Unit
    }
}

/** An arrow that bounces over the sprint button, and the words that say what to do with it. */
private fun DrawScope.drawZoomLesson(world: GameWorld, texts: SceneTexts) {
    val arena = world.arena
    val centerX = SprintZone.buttonX(arena, world.sprintSide).roundToInt()
    val bounce = (sin(world.time * 6f) * 2f).roundToInt()
    val tipY = SprintZone.buttonY(arena).roundToInt() - SprintZone.buttonRadius.toInt() - 3 + bounce
    drawArrow(centerX, tipY, ArrowDirection.Down)
    val textWidth = PixelFont.width(texts.holdToZoom, 2)
    val left = when (world.sprintSide) {
        SprintSide.Left -> zoomTextMargin
        SprintSide.Right -> arena.width.toInt() - zoomTextMargin - textWidth
    }
    drawPixelText(texts.holdToZoom, left, tipY - arrowLength - 4 - PixelFont.glyphHeight * 2, HudPalette.gold, scale = 2)
}

/** A big arrow that bounces under the hunger bar in the top right corner and points up at it, with the words under it. */
private fun DrawScope.drawHungerLesson(world: GameWorld, text: String, topInset: Int) {
    val width = world.arena.width.toInt()
    val bounce = (sin(world.time * 6f) * 2f).roundToInt()
    val tipY = topInset + stomachBottom + 3 + bounce
    drawArrow(width - stomachCenterFromEdge, tipY, ArrowDirection.Up)
    val top = tipY + arrowLength + 4
    text.lines().forEachIndexed { index, line ->
        drawPixelText(line, width - zoomTextMargin - PixelFont.width(line, 2), top + index * lineHeight, HudPalette.gold, scale = 2)
    }
}

/** The words of a lesson, over the top of the screen, in the middle. */
private fun DrawScope.drawLessonText(world: GameWorld, text: String, topInset: Int) {
    text.lines().forEachIndexed { index, line ->
        drawCenteredPixelText(line, world.arena.width.toInt() / 2, topInset + lessonFromTop + index * lineHeight, HudPalette.gold, scale = 2)
    }
}

/** How far above the middle of the house the lesson of the turn is. */
private const val lessonAboveHouse = 66

/** How far the words of a lesson are from the edge of the screen. */
private const val zoomTextMargin = 3

/** The height of one line of the words of a lesson, and how far under the status bar a lesson that is in the middle starts. */
private const val lineHeight = 13
private const val lessonFromTop = 24

/** Where the hunger bar is: how far its bottom is under the status bar, and its middle from the right edge of the screen. */
private const val stomachBottom = 19
private const val stomachCenterFromEdge = 12

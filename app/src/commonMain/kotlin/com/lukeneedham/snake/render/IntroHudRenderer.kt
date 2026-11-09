package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.IntroStage
import com.lukeneedham.snake.game.SprintSide
import com.lukeneedham.snake.game.SprintZone
import kotlin.math.roundToInt
import kotlin.math.sin

/** The way an arrow points. */
private enum class ArrowDirection { Up, Down, Left, Right }

/**
 * What is on the screen during the story and the lessons at the start. Nothing while the story plays by itself; the lesson of the turn once
 * the bubble has popped, and an arrow to the path to the left; in the room of the tutorial the hunger bar, with an arrow and the words that
 * say that the belly decreases, then the sprint button, with an arrow and the lesson of the sprint, and the words that zooming makes the snake
 * hungry, then the words of the lessons of the mouse, the toad and the bird; then an arrow that shows the way back to the house, and one
 * that shows the way up to the stage. The bars and the banners of the effects and the power ups are there once the animals come.
 */
internal fun DrawScope.drawIntroHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val intro = world.intro ?: return
    if (intro.showsHunger) drawStomach(world, topInset)
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
        IntroStage.Steering -> if (intro.isTutorial) drawEdgeArrow(world, ArrowDirection.Left, topInset)
        IntroStage.Hunger -> drawHungerLesson(world, texts.bellyDecreases, topInset)
        IntroStage.Sprint -> drawZoomLesson(world, texts)
        IntroStage.Zoomed -> drawLessonText(world, texts.zoomingMakesHungry, topInset)
        IntroStage.Mouse -> drawHungerLesson(world, texts.eatToNotStarve, topInset)
        IntroStage.Toad -> drawLessonText(world, texts.youCanEatToads, topInset)
        IntroStage.Dizzy -> drawLessonText(world, texts.butTheyPoison, topInset)
        IntroStage.Bird -> drawLessonText(world, texts.tryToCatchBirds, topInset)
        IntroStage.Powers -> drawLessonText(world, texts.theyGivePowers, topInset)
        IntroStage.Return -> drawEdgeArrow(world, ArrowDirection.Right, topInset)
        IntroStage.Leaving -> drawEdgeArrow(world, ArrowDirection.Up, topInset)
        else -> Unit
    }
}

/** A big arrow that bounces at the edge of the screen and points the way to go: left to the path to the room, right back to the house, up to the stage. */
private fun DrawScope.drawEdgeArrow(world: GameWorld, direction: ArrowDirection, topInset: Int) {
    val width = world.arena.width.toInt()
    val height = world.arena.height.toInt()
    val bounce = (sin(world.time * 6f) * 2f).roundToInt()
    when (direction) {
        ArrowDirection.Left -> drawArrow(edgeMargin + bounce, height / 2, direction)
        ArrowDirection.Right -> drawArrow(width - edgeMargin - bounce, height / 2, direction)
        ArrowDirection.Up -> drawArrow(width / 2, topInset + upFromTop + bounce, direction)
        ArrowDirection.Down -> drawArrow(width / 2, height - edgeMargin - bounce, direction)
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

/** A big arrow with its tip at [tipX], [tipY]: a head and a stem, pointing the way of [direction]. */
private fun DrawScope.drawArrow(tipX: Int, tipY: Int, direction: ArrowDirection) {
    for (outline in booleanArrayOf(true, false)) {
        for (row in 0 until arrowLength) {
            val thickness = if (row < arrowHead) 1 + row * 2 else 3
            val color = if (outline) PixelFont.outline else HudPalette.gold
            val grow = if (outline) 1 else 0
            when (direction) {
                ArrowDirection.Up -> pixelRect(tipX - thickness / 2 - grow, tipY + row - grow, thickness + grow * 2, 1 + grow * 2, color)
                ArrowDirection.Down -> pixelRect(tipX - thickness / 2 - grow, tipY - row - grow, thickness + grow * 2, 1 + grow * 2, color)
                ArrowDirection.Left -> pixelRect(tipX + row - grow, tipY - thickness / 2 - grow, 1 + grow * 2, thickness + grow * 2, color)
                ArrowDirection.Right -> pixelRect(tipX - row - grow, tipY - thickness / 2 - grow, 1 + grow * 2, thickness + grow * 2, color)
            }
        }
    }
}

/** How far above the middle of the house the lesson of the turn is. */
private const val lessonAboveHouse = 66

/** How far the words of a lesson are from the edge of the screen. */
private const val zoomTextMargin = 3

/** The height of one line of the words of a lesson, and how far under the status bar a lesson that is in the middle starts. */
private const val lineHeight = 13
private const val lessonFromTop = 24

/** The arrow is this long, and its head the first rows of that. */
private const val arrowLength = 12
private const val arrowHead = 5

/** How far the tip of an arrow at the edge of the screen is from it, and how far under the status bar the arrow that points up is. */
private const val edgeMargin = 5
private const val upFromTop = 10

/** Where the hunger bar is: how far its bottom is under the status bar, and its middle from the right edge of the screen. */
private const val stomachBottom = 19
private const val stomachCenterFromEdge = 12

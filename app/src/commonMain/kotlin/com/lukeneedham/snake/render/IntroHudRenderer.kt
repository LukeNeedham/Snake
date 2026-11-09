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
 * the bubble has popped; from half way along the path the sprint button, with an arrow and the lesson of the sprint until it is learned;
 * then the hunger bar, with an arrow and its lesson, and the lessons of the toad and the bird. The bars and the banners of the effects and
 * the power ups are there once the animals come.
 */
internal fun DrawScope.drawIntroHud(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val intro = world.intro ?: return
    if (intro.showsHunger) drawStomach(world, topInset)
    if (intro.reachedHalfway) drawSprintButton(world)
    if (intro.stage.isLesson) {
        drawEffectBars(world)
        drawEffectBanner(world, texts)
        drawPowerUpBanner(world, texts)
    }
    when (intro.stage) {
        IntroStage.Teaching -> {
            // Where the big bubble was, on the screen
            val top = (world.den.denY - world.cameraY).roundToInt() - lessonAboveDen
            val bounce = (sin(world.time * 4f) * 1.2f).roundToInt()
            drawCenteredPixelText(texts.tapToTurn, world.arena.width.toInt() / 2, top + bounce, HudPalette.gold, scale = 2)
        }
        IntroStage.Steering -> if (intro.reachedHalfway && !intro.hasZoomed) drawZoomLesson(world, texts)
        IntroStage.Hunger, IntroStage.Mouse -> drawHungerLesson(world, texts, topInset)
        IntroStage.Toad, IntroStage.Dizzy -> drawLessonText(world, texts.toadsPoisonYou, topInset)
        IntroStage.Bird -> drawLessonText(world, texts.birdsGivePowers, topInset)
        else -> Unit
    }
}

/** An arrow that bounces over the sprint button, and the words that say what to do with it. */
private fun DrawScope.drawZoomLesson(world: GameWorld, texts: SceneTexts) {
    val arena = world.arena
    val centerX = SprintZone.buttonX(arena, world.sprintSide).roundToInt()
    val bounce = (sin(world.time * 6f) * 2f).roundToInt()
    val tipY = SprintZone.buttonY(arena).roundToInt() - SprintZone.buttonRadius.toInt() - 3 + bounce
    drawArrow(centerX, tipY, pointsUp = false)
    val textWidth = PixelFont.width(texts.holdToZoom, 2)
    val left = when (world.sprintSide) {
        SprintSide.Left -> zoomTextMargin
        SprintSide.Right -> arena.width.toInt() - zoomTextMargin - textWidth
    }
    drawPixelText(texts.holdToZoom, left, tipY - arrowLength - 4 - PixelFont.glyphHeight * 2, HudPalette.gold, scale = 2)
}

/** A big arrow that bounces under the hunger bar in the top right corner and points up at it, with the words under it. */
private fun DrawScope.drawHungerLesson(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    val bounce = (sin(world.time * 6f) * 2f).roundToInt()
    val tipY = topInset + stomachBottom + 3 + bounce
    drawArrow(width - stomachCenterFromEdge, tipY, pointsUp = true)
    val top = tipY + arrowLength + 4
    texts.eatToNotStarve.lines().forEachIndexed { index, line ->
        drawPixelText(line, width - zoomTextMargin - PixelFont.width(line, 2), top + index * lineHeight, HudPalette.gold, scale = 2)
    }
}

/** The words of a lesson, over the top of the screen, in the middle. */
private fun DrawScope.drawLessonText(world: GameWorld, text: String, topInset: Int) {
    text.lines().forEachIndexed { index, line ->
        drawCenteredPixelText(line, world.arena.width.toInt() / 2, topInset + lessonFromTop + index * lineHeight, HudPalette.gold, scale = 2)
    }
}

/** A big arrow with its tip at [tipX], [tipY]: a head and a stem, pointing up or down. */
private fun DrawScope.drawArrow(tipX: Int, tipY: Int, pointsUp: Boolean) {
    // Rows from the tip: the head widens, then the stem
    for (row in 0 until arrowLength) {
        val width = if (row < arrowHead) 1 + row * 2 else 3
        val y = if (pointsUp) tipY + row else tipY - row
        pixelRect(tipX - width / 2 - 1, y - 1, width + 2, 3, PixelFont.outline)
    }
    for (row in 0 until arrowLength) {
        val width = if (row < arrowHead) 1 + row * 2 else 3
        val y = if (pointsUp) tipY + row else tipY - row
        pixelRect(tipX - width / 2, y, width, 1, HudPalette.gold)
    }
}

/** How far above the middle of the den the lesson of the turn is. */
private const val lessonAboveDen = 66

/** How far the words of a lesson are from the edge of the screen. */
private const val zoomTextMargin = 3

/** The height of one line of the words of a lesson, and how far under the status bar a lesson that is in the middle starts. */
private const val lineHeight = 13
private const val lessonFromTop = 24

/** The arrow is this long, and its head the first rows of that. */
private const val arrowLength = 12
private const val arrowHead = 5

/** Where the hunger bar is: how far its bottom is under the status bar, and its middle from the right edge of the screen. */
private const val stomachBottom = 19
private const val stomachCenterFromEdge = 12

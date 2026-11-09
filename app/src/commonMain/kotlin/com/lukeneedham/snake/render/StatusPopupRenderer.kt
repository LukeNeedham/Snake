package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.GameWorld
import com.lukeneedham.snake.game.StatusEntry

private const val margin = 6
private const val textLeft = margin + ArrowIcon.width + 4
private const val lineHeight = 7
private const val entryGap = 5

/** The text beside the arrows is this many characters wide at most; the descriptions wrap to fit. */
private const val lineChars = 21

/**
 * The third page of the pause overlay, the list of what is on: for each effect and power up its arrow, filled by the time left, its
 * name, the seconds left on the right, and under them what it does.
 */
internal fun DrawScope.drawStatusPage(world: GameWorld, texts: SceneTexts, topInset: Int) {
    val width = world.arena.width.toInt()
    drawCenteredPixelText(texts.statusTitle, width / 2, topInset + PageTitle.top, HudPalette.gold, scale = 2)
    val statuses = world.statuses
    if (statuses.isEmpty()) {
        drawCenteredPixelText(texts.statusEmpty, width / 2, world.arena.height.toInt() / 2 - 3, HudPalette.white.copy(alpha = 0.7f))
        return
    }
    var y = topInset + 28
    for (entry in statuses) {
        y += drawEntry(entry, texts, width, y) + entryGap
    }
}

/** Draws one entry with its top at [top], and returns how tall it is. */
private fun DrawScope.drawEntry(entry: StatusEntry, texts: SceneTexts, width: Int, top: Int): Int {
    val colors = statusColors(entry)
    drawFilledArrow(margin, top, entry.isPowerUp, entry.fraction, colors.arrow)
    val effect = entry.effect
    val powerUp = entry.powerUp
    val name = if (powerUp != null) texts.powerUpNames[powerUp.ordinal] else texts.effectNames[effect!!.ordinal]
    val description = if (powerUp != null) texts.powerUpDescriptions[powerUp.ordinal] else texts.effectDescriptions[effect!!.ordinal]
    drawPixelText(name, textLeft, top, colors.text)
    val seconds = "${entry.seconds}s"
    drawPixelText(seconds, width - margin - PixelFont.width(seconds, scale = 1), top, HudPalette.white)
    val lines = wrapWords(description, lineChars)
    lines.forEachIndexed { index, line ->
        drawPixelText(line, textLeft, top + lineHeight + 2 + index * lineHeight, HudPalette.white.copy(alpha = 0.8f))
    }
    return maxOf(ArrowIcon.height, lineHeight + 2 + lines.size * lineHeight)
}

/** Splits [text] into lines of at most [maxChars] characters, only between words. */
internal fun wrapWords(text: String, maxChars: Int): List<String> {
    val lines = ArrayList<String>()
    var line = ""
    for (word in text.split(' ')) {
        if (line.isNotEmpty() && line.length + 1 + word.length > maxChars) {
            lines.add(line)
            line = word
        } else {
            line = if (line.isEmpty()) word else "$line $word"
        }
    }
    if (line.isNotEmpty()) lines.add(line)
    return lines
}

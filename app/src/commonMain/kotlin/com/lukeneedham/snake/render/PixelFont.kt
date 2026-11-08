package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/** A tiny font of 3 by 5 pixels per letter: each glyph is its five rows of three pixels, one after the other. */
internal object PixelFont {
    const val glyphWidth = 3
    const val glyphHeight = 5

    /** The colour of the outline that every text has. */
    val outline = Color(0xFF1B1422)

    private val glyphs: Map<Char, String> = mapOf(
        'A' to "010101111101101", 'B' to "110101110101110", 'C' to "011100100100011", 'D' to "110101101101110",
        'E' to "111100110100111", 'F' to "111100110100100", 'G' to "011100101101011", 'H' to "101101111101101",
        'I' to "111010010010111", 'J' to "001001001101010", 'K' to "101101110101101", 'L' to "100100100100111",
        'M' to "101111111101101", 'N' to "110101101101101", 'O' to "010101101101010", 'P' to "110101110100100",
        'Q' to "010101101110011", 'R' to "110101110101101", 'S' to "011100010001110", 'T' to "111010010010010",
        'U' to "101101101101111", 'V' to "101101101101010", 'W' to "101101111111101", 'X' to "101101010101101",
        'Y' to "101101010010010", 'Z' to "111001010100111",
        '0' to "111101101101111", '1' to "010110010010111", '2' to "110001010100111", '3' to "110001010001110",
        '4' to "101101111001001", '5' to "111100110001110", '6' to "011100111101111", '7' to "111001010010010",
        '8' to "111101111101111", '9' to "111101111001110",
        '!' to "010010010000010", '?' to "110001010000010", '+' to "000010111010000", '-' to "000000111000000",
        ':' to "000010000010000", '.' to "000000000000010",
    )

    fun width(text: String, scale: Int): Int = if (text.isEmpty()) 0 else text.length * (glyphWidth + 1) * scale - scale

    inline fun forEachPixel(text: String, block: (column: Int, row: Int) -> Unit) {
        text.forEachIndexed { index, char ->
            val glyph = glyph(char) ?: return@forEachIndexed
            for (row in 0 until glyphHeight) {
                for (column in 0 until glyphWidth) {
                    if (glyph[row * glyphWidth + column] == '1') block(index * (glyphWidth + 1) + column, row)
                }
            }
        }
    }

    fun glyph(char: Char): String? = glyphs[char.uppercaseChar()]
}

/** Draws [text] with its top left corner at [x], [y]: an outline one art pixel thick is drawn first. */
internal fun DrawScope.drawPixelText(
    text: String,
    x: Int,
    y: Int,
    color: Color,
    scale: Int = 1,
    outline: Color = PixelFont.outline,
) {
    PixelFont.forEachPixel(text) { column, row ->
        pixelRect(x + column * scale - 1, y + row * scale - 1, scale + 2, scale + 2, outline)
    }
    PixelFont.forEachPixel(text) { column, row ->
        pixelRect(x + column * scale, y + row * scale, scale, scale, color)
    }
}

internal fun DrawScope.drawCenteredPixelText(
    text: String,
    centerX: Int,
    y: Int,
    color: Color,
    scale: Int = 1,
    outline: Color = PixelFont.outline,
) {
    drawPixelText(text, centerX - PixelFont.width(text, scale) / 2, y, color, scale, outline)
}

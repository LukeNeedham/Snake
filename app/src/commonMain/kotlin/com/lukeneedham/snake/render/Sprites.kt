package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lukeneedham.snake.game.ToadKind

/** The animals, facing right. Each letter is a colour of [spriteColor]; a dot is empty. */
internal enum class SpriteArt(val rows: List<String>) {
    MOUSE(
        listOf(
            "....oo......",
            "...opgoo....",
            "..oggggoooo.",
            "ooggggggegpo",
            "o.oGGGGgggo.",
            "..oGoGGooo..",
            "..oo.oo.....",
        ),
    ),
    TOAD(
        listOf(
            "...oooo.....",
            "..owebboo...",
            ".obbbbbbbo..",
            "obbbdbbdbbo.",
            "obbbbbbbbbeo",
            "oblllllllbo.",
            ".oo.ooo.oo..",
        ),
    ),
    BUNNY(
        listOf(
            "....oo.oo...",
            "...opGoGpo..",
            "...oGGoGGo..",
            "..oGGGGGGoo.",
            ".oGGGGGGeGpo",
            "oGGGGGGGGGo.",
            ".oGGoGGGoo..",
            "..oo.oo.....",
        ),
    ),
    CHICK(
        listOf(
            "....oo......",
            "...oyyoo....",
            "..oyyyyyo...",
            ".oyyeyyyroo.",
            ".oyyyyyyrro.",
            ".oyYYYyyoo..",
            "..oyyyyo....",
            "...r..r.....",
        ),
    ),
    BEETLE(
        listOf(
            "..o....o....",
            "...o..o.....",
            "..oooooo....",
            ".okkskkkko..",
            ".okskkkkkko.",
            ".okkkkkkkeo.",
            "..oooooooo..",
            "..o.o..o.o..",
        ),
    ),
    HEDGEHOG(
        listOf(
            "..o.o.o.o...",
            ".ohohohohoo.",
            "ohHhHhHhHhoo",
            "ohhhhhhhhNeo",
            "ohhhhhhhhNNp",
            ".ohhhhhhooo.",
            "..oo..oo....",
        ),
    ),
    ;

    val width: Int get() = rows.maxOf { it.length }
    val height: Int get() = rows.size

    companion object {
        /** What the animals look like while the snake is disguise-blind: never a mouse or a toad. */
        val disguises: List<SpriteArt> = listOf(BUNNY, CHICK, BEETLE, HEDGEHOG)
    }
}

/** [kind] only matters for the toad body letters: `b` body, `d` shade, `l` light. */
internal fun spriteColor(letter: Char, kind: ToadKind?): Color? {
    val toad = kind ?: ToadKind.PURPLE
    return when (letter) {
        'o' -> Color(0xFF2B2230)
        'g' -> Color(0xFFA39AA8)
        'G' -> Color(0xFFD9D2DC)
        'p' -> Color(0xFFF28FA9)
        'e' -> Color(0xFF151218)
        'w' -> Color(0xFFFFFFFF)
        'b' -> toad.body
        'd' -> toad.shade
        'l' -> toad.light
        'y' -> Color(0xFFFFD84A)
        'Y' -> Color(0xFFFFEF9A)
        'r' -> Color(0xFFFF8A3D)
        'k' -> Color(0xFF3A3340)
        's' -> Color(0xFF8C82A0)
        'h' -> Color(0xFF6B4A35)
        'H' -> Color(0xFF9C7452)
        'N' -> Color(0xFFE3B88F)
        else -> null
    }
}

internal fun DrawScope.drawSprite(art: SpriteArt, kind: ToadKind?, left: Int, top: Int, flip: Boolean) {
    val width = art.width
    art.rows.forEachIndexed { row, line ->
        var column = 0
        while (column < line.length) {
            val letter = line[column]
            val color = spriteColor(letter, kind)
            if (color == null) {
                column++
                continue
            }
            var end = column
            while (end + 1 < line.length && line[end + 1] == letter) end++
            val runLeft = if (flip) left + (width - 1 - end) else left + column
            pixelRect(runLeft, top + row, end - column + 1, 1, color)
            column = end + 1
        }
    }
}

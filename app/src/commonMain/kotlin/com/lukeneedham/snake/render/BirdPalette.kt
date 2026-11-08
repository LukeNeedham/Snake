package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color
import com.lukeneedham.snake.game.BirdKind

/** The colours of a kind of bird: one for each letter of the [BirdArt], and the two that its power up bar and banner are drawn in. */
internal class BirdPalette(
    private val outline: Color,
    private val back: Color,
    private val wing: Color,
    private val belly: Color,
    private val cap: Color,
    private val cheek: Color,
    private val beak: Color,
    private val eye: Color,
    private val feet: Color,
    val bar: Color,
    val barLight: Color,
) {
    fun colorOf(letter: Char): Color? = when (letter) {
        'o' -> outline
        'b' -> back
        'w' -> wing
        'l' -> belly
        'c' -> cap
        'x' -> cheek
        'k' -> beak
        'e' -> eye
        'f' -> feet
        else -> null
    }
}

private val darkEye = Color(0xFF151218)
private val brightEye = Color(0xFFF5F5F5)
private val lightFeet = Color(0xFF8A6A5A)

internal fun birdPalette(kind: BirdKind): BirdPalette = when (kind) {
    BirdKind.BlueTit -> BirdPalette(
        Color(0xFF2B2230), Color(0xFF9CB45A), Color(0xFF4A7FD0), Color(0xFFF4D84A), Color(0xFF4A7FD0),
        Color(0xFFFFFFFF), Color(0xFF3A3340), darkEye, lightFeet, Color(0xFF4A7FD0), Color(0xFFB5D0F5),
    )
    BirdKind.Robin -> BirdPalette(
        Color(0xFF2B2230), Color(0xFF9A7B58), Color(0xFF7A5C3E), Color(0xFFE9E1D0), Color(0xFF9A7B58),
        Color(0xFFE8742A), Color(0xFF3A3340), darkEye, lightFeet, Color(0xFFE8742A), Color(0xFFFFC9A0),
    )
    BirdKind.Sparrow -> BirdPalette(
        Color(0xFF2B2230), Color(0xFF8B6B4A), Color(0xFF6A4A32), Color(0xFFD8CDB8), Color(0xFF9A5A3A),
        Color(0xFFE8E0D0), Color(0xFF4A4038), darkEye, lightFeet, Color(0xFFA9794F), Color(0xFFEBD3B4),
    )
    BirdKind.Blackbird -> BirdPalette(
        Color(0xFF1B1422), Color(0xFF3F3947), Color(0xFF2E2938), Color(0xFF4A4455), Color(0xFF3F3947),
        Color(0xFF3F3947), Color(0xFFF5A623), brightEye, lightFeet, Color(0xFFF5A623), Color(0xFFFFE0A0),
    )
    BirdKind.Crow -> BirdPalette(
        Color(0xFF1B1422), Color(0xFF454A62), Color(0xFF343850), Color(0xFF505672), Color(0xFF454A62),
        Color(0xFF454A62), Color(0xFF8A8A9A), brightEye, Color(0xFF6A6A7A), Color(0xFF7C84B0), Color(0xFFCDD2F0),
    )
    BirdKind.Pigeon -> BirdPalette(
        Color(0xFF2B2230), Color(0xFF8E96A6), Color(0xFF6E7688), Color(0xFFA9B0BE), Color(0xFF8E96A6),
        Color(0xFF5FB59A), Color(0xFFE8B4B8), Color(0xFFE8742A), Color(0xFFC06A7A), Color(0xFF5FB59A), Color(0xFFC4F0E2),
    )
}

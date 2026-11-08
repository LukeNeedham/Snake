package com.lukeneedham.snake.render

import androidx.compose.ui.graphics.Color

/** The greens of the tall grass, from the dark floor to the light tips of the blades. */
internal object GrassPalette {
    val base = Color(0xFF1C4528)
    val speckDark = Color(0xFF173B22)
    val speckLight = Color(0xFF235632)
    val bladesBack = listOf(Color(0xFF1F4F2C), Color(0xFF2A6034), Color(0xFF357A3E), Color(0xFF45934A))
    val bladesFront = listOf(Color(0xFF2F7339), Color(0xFF3E8B45), Color(0xFF55A453))
    val bladeTip = Color(0xFF9BD577)
}

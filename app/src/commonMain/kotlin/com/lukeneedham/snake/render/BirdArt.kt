package com.lukeneedham.snake.render

/**
 * The birds, facing right: a small and a large one, each on the ground and with its wings up and down for flying. Each letter is
 * a colour of the [BirdPalette] of the kind of bird: `o` outline, `b` back, `w` wing, `l` belly, `c` cap, `x` cheek and breast,
 * `k` beak, `e` eye, `f` feet; a dot is empty.
 */
internal enum class BirdArt(val rows: List<String>) {
    SmallPerched(
        listOf(
            "....ooo..",
            "...occco.",
            "..occcekk",
            ".owxxxxo.",
            "owwbxxlo.",
            "owwbllllo",
            ".owwllloo",
            "..oo.f.f.",
        ),
    ),
    SmallWingsUp(
        listOf(
            "..wo.....",
            ".owwo.ooo",
            "owwbooccc",
            ".owbbcekk",
            "..obxxxo.",
            "..oollllo",
            "...ollloo",
            "....ooo..",
        ),
    ),
    SmallWingsDown(
        listOf(
            "....ooo..",
            "...occco.",
            "..occcekk",
            ".obbxxxo.",
            "oobbxxllo",
            "owwwbllo.",
            "owwwwwo..",
            ".owwwo...",
        ),
    ),
    LargePerched(
        listOf(
            "......oooo..",
            ".....occcco.",
            "....occccexk",
            "....occccokk",
            "oo.oxxxxxo..",
            "owwobbxxxxo.",
            "owwwbbbxxllo",
            "owwwbbbbllllo",
            ".owwwbbbllllo",
            "..owwwbblllo.",
            "...ooooooooo.",
            ".....f..f....",
        ),
    ),
    LargeWingsUp(
        listOf(
            "..ww.........",
            ".owwwo..oooo.",
            "owwwwwooccco.",
            "owwwwwbbcccexk",
            ".owwwbbbbccokk",
            "..oobbbbxxxo..",
            "...obbbbllllo.",
            "...oobbllllllo",
            "....oollllllo.",
            ".....ooooooo..",
        ),
    ),
    LargeWingsDown(
        listOf(
            "......oooo..",
            ".....occcco.",
            "....occccexk",
            "ooo.occccokk",
            "owwobbxxxo..",
            "owwwbbbxxxo.",
            "owwwbbbbllllo",
            ".owwwbbblllo.",
            "..owwwwwllo..",
            "...owwwwwo...",
            "....owwwo....",
        ),
    ),
    ;

    val width: Int get() = rows.maxOf { it.length }
    val height: Int get() = rows.size
}

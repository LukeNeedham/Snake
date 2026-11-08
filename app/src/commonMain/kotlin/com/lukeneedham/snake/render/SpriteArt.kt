package com.lukeneedham.snake.render

/** The animals, facing right. The toad is a frog that looks at you: eyes on top of its head, a wide mouth and a pale belly. Each letter is a colour of [spriteColor]; a dot is empty. */
internal enum class SpriteArt(val rows: List<String>) {
    Mouse(
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
    Toad(
        listOf(
            "..oooo....oooo..",
            ".owwwwo..owwwwo.",
            ".owweeo..oeewwo.",
            ".obbbbbbbbbbbbo.",
            "obbbbbbbbbbbbbbo",
            "obbbbbbbbbbbbbbo",
            "oboooooooooooobo",
            "obbbllllllllbbbo",
            "obbdlllllllldbbo",
            "oddbblloollbbddo",
            "obbbdbo..obdbbbo",
            "oooooo....oooooo",
        ),
    ),
    Beetle(
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
    Hedgehog(
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
        /** What the animals look like while the snake is disguise-blind: weird monsters, never a real animal, a mouse or a toad. */
        val disguises: List<SpriteArt> = listOf(Beetle, Hedgehog)
    }
}

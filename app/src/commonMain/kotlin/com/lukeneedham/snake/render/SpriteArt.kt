package com.lukeneedham.snake.render

/** The animals, facing right. The toad is a frog: eyes on top of its head, long back legs and a pale belly. Each letter is a colour of [spriteColor]; a dot is empty. */
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
            "........oo.oo.",
            ".......owwowwo",
            ".......oeeoeeo",
            "...ooooobbbbbo",
            "..obbbbbbbbbbo",
            ".obbdbbbbbbbbe",
            ".obdbbblllllbo",
            "obbbolllllllo.",
            "oooo.ooooo.oo.",
        ),
    ),
    Bunny(
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
    Chick(
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
        /** What the animals look like while the snake is disguise-blind: never a mouse or a toad. */
        val disguises: List<SpriteArt> = listOf(Bunny, Chick, Beetle, Hedgehog)
    }
}

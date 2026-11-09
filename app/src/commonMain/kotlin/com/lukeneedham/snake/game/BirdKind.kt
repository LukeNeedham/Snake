package com.lukeneedham.snake.game

/** How big a bird is: small ones are tits, robins and sparrows, large ones are blackbirds, crows and pigeons. */
enum class BirdSize { Small, Large }

/**
 * A kind of bird. Eating one gives its [powerUp]. A bird flies off when the snake comes closer than its [alarmDistance]: the
 * small ones are the most skittish, the bold crow lets the snake come closest.
 */
enum class BirdKind(val powerUp: PowerUp, val size: BirdSize, val alarmDistance: Float) {
    BlueTit(PowerUp.Zoom, BirdSize.Small, 18f),
    Robin(PowerUp.FastSprint, BirdSize.Small, 17f),
    Sparrow(PowerUp.Sneaky, BirdSize.Small, 19f),
    Blackbird(PowerUp.BiggerMouth, BirdSize.Large, 16f),
    Crow(PowerUp.ToadFree, BirdSize.Large, 14f),
    Pigeon(PowerUp.Cheese, BirdSize.Large, 15f),
}

package com.lukeneedham.snake.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WorldLayoutTest {

    private val arena = Arena(108f, 234f)
    private val layout = WorldLayout(arena)

    @Test
    fun thePathBetweenTheStageAndTheHouseIsAboutOneScreenLong() {
        val stageBottom = arena.height - WorldTuning.grassDepth
        val houseTop = layout.houseY - layout.house.radiusAt(-1.5707964f)
        assertTrue(houseTop - stageBottom >= arena.height * 0.95f, "the path is ${houseTop - stageBottom} long")
    }

    @Test
    fun theStageCannotBeSeenFromTheHouseNorTheHouseFromTheStage() {
        // The camera in the house: its top edge is under the stage
        assertTrue(layout.maxCameraY > arena.height)
        // The camera on the stage: its bottom edge is above the house
        val houseTop = layout.houseY - layout.house.radiusAt(-1.5707964f)
        assertTrue(houseTop > arena.height)
    }

    @Test
    fun theRoomOfTheTutorialIsToTheLeftOfTheHouseAtTheSameHeight() {
        assertTrue(layout.room.centerX < layout.houseX - arena.width / 2f)
        assertEquals(layout.houseY, layout.room.centerY)
        assertEquals(layout.worldLeft, layout.roomCameraX)
    }

    @Test
    fun theWholeMapIsConnectedForTheSnakeWhileTheGateIsOpen() {
        layout.isGateClosed = false
        // The middle of the stage, along the path up, the house, the path to the left, and the middle of the room
        val way = listOf(
            arena.centerX to arena.centerY,
            arena.centerX to arena.height + 20f,
            arena.centerX to layout.houseY - 60f,
            layout.houseX to layout.houseY,
            0f to layout.houseY,
            layout.room.centerX to layout.room.centerY,
        )
        for ((x, y) in way) assertTrue(layout.ground.signedDistance(x, y) < 0f, "($x, $y) is not on the ground")
    }

    @Test
    fun theStonesBlockThePathUpWhileTheGateIsClosed() {
        layout.isGateClosed = true
        assertTrue(layout.ground.signedDistance(layout.gate.centerX, layout.gate.centerY) > 0f)
        // But the house, the path to the left and the room are not blocked
        assertTrue(layout.ground.signedDistance(layout.houseX, layout.houseY) < 0f)
        assertTrue(layout.ground.signedDistance(0f, layout.houseY) < 0f)
        layout.isGateClosed = false
        assertTrue(layout.ground.signedDistance(layout.gate.centerX, layout.gate.centerY) < 0f)
    }

    @Test
    fun theStoneBlockingThePathIsAboveTheHouse() {
        assertTrue(layout.gate.centerY < layout.houseY - layout.house.radiusAt(-1.5707964f))
        assertFalse(layout.house.signedDistance(layout.gate.centerX, layout.gate.centerY) < 0f)
    }

    @Test
    fun theHouseIsNotRound() {
        val radii = (0 until 24).map { layout.house.radiusAt(it * 0.2618f) }
        assertTrue(radii.max() - radii.min() > 4f)
    }
}

package com.vadimg778.signalmonitor.feature.monitor.domain.model

import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SignalGeneratorTest {

    @Test
    fun `appends points without replacing existing history`() {
        val generator = generator()
        val points = listOf(
            SignalPoint(timestampMillis = 1_000L, value = 1.0),
            SignalPoint(timestampMillis = 2_000L, value = 2.0),
        )

        val updatedGenerator = generator.append(points)

        assertEquals(listOf(generator.points.first()) + points, updatedGenerator.points)
        assertEquals(2.0, updatedGenerator.currentValue, 0.0)
    }

    @Test
    fun `completes when no lifetime remains`() {
        val completedGenerator = generator().updateRemainingTime(elapsedRealtimeMillis = 10_000L)

        assertEquals(0L, completedGenerator.remainingTimeMillis)
        assertEquals(GeneratorStatus.COMPLETED, completedGenerator.status)
    }

    @Test
    fun `rejects state without a current point or with contradictory completion`() {
        assertThrows(IllegalArgumentException::class.java) {
            generator().copy(points = persistentListOf())
        }
        assertThrows(IllegalArgumentException::class.java) {
            generator().copy(status = GeneratorStatus.COMPLETED)
        }
    }

    private fun generator() = SignalGenerator(
        id = GeneratorId(1),
        name = "Generator #1",
        color = SignalColor(0),
        expiresAtElapsedRealtimeMillis = 10_000L,
        remainingTimeMillis = 10_000L,
        status = GeneratorStatus.ACTIVE,
        points = persistentListOf(SignalPoint(timestampMillis = 0L, value = 0.0)),
    )
}

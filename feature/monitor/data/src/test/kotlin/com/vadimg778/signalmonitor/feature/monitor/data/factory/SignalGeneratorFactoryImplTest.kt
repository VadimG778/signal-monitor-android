package com.vadimg778.signalmonitor.feature.monitor.data.factory

import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SignalGeneratorFactoryImplTest {

    @Test
    fun `creates ten unique active generators within configured ranges`() {
        val startedAtMillis = 10_000L
        val startedAtElapsedRealtimeMillis = 20_000L
        val factory = SignalGeneratorFactoryImpl(
            timeProvider = object : TimeProvider {

                override fun currentTimeMillis() = startedAtMillis

                override fun elapsedRealtimeMillis() = startedAtElapsedRealtimeMillis
            },
            random = Random(42),
        )

        val generators = factory.create(10)

        assertEquals(10, generators.size)
        assertEquals(10, generators.map { it.id }.toSet().size)
        assertEquals(10, generators.map { it.name }.toSet().size)
        assertEquals(10, generators.map { it.color }.toSet().size)
        assertTrue(generators.all { it.status == GeneratorStatus.ACTIVE })
        assertTrue(generators.all { it.remainingTimeMillis in 60_000L..1_799_000L })
        assertTrue(
            generators.all {
                val expectedExpirationTime = startedAtElapsedRealtimeMillis +
                    it.remainingTimeMillis
                it.expiresAtElapsedRealtimeMillis == expectedExpirationTime
            },
        )
        assertTrue(generators.all { it.currentValue in -1.0..1.0 })
        assertTrue(generators.all { it.points.single().timestampMillis == startedAtMillis })
    }
}

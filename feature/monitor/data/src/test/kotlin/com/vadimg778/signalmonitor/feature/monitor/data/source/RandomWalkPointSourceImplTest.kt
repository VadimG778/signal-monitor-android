package com.vadimg778.signalmonitor.feature.monitor.data.source

import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class RandomWalkPointSourceImplTest {

    @Test
    fun `emits an independent random walk point every second`() = runTest {
        val source = RandomWalkPointSourceImpl(
            timeProvider = testTimeProvider { testScheduler.currentTime },
            random = Random(7),
        )
        val points = async {
            source
                .observePoints(
                    initialPoint = SignalPoint(timestampMillis = 0L, value = 0.5),
                    expiresAtElapsedRealtimeMillis = 3_000L,
                )
                .take(2)
                .toList()
        }

        runCurrent()
        advanceTimeBy(2.seconds)
        runCurrent()

        val emittedPoints = points.await()
        assertEquals(listOf(1_000L, 2_000L), emittedPoints.map { it.timestampMillis })
        assertTrue(emittedPoints.first().value - 0.5 in -1.0..1.0)
        assertTrue(emittedPoints.last().value - emittedPoints.first().value in -1.0..1.0)
    }

    @Test
    fun `stops before emitting at its expiration time`() = runTest {
        val source = RandomWalkPointSourceImpl(
            timeProvider = testTimeProvider { testScheduler.currentTime },
            random = Random(7),
        )
        val points = async {
            source
                .observePoints(
                    initialPoint = SignalPoint(timestampMillis = 0L, value = 0.0),
                    expiresAtElapsedRealtimeMillis = 2_000L,
                )
                .toList()
        }

        runCurrent()
        advanceTimeBy(2.seconds)
        runCurrent()

        assertEquals(listOf(1_000L), points.await().map { it.timestampMillis })
    }

    @Test
    fun `keeps timestamps ascending when wall clock moves backwards`() = runTest {
        var wallClockMillis = 10_000L
        val source = RandomWalkPointSourceImpl(
            timeProvider = object : TimeProvider {

                override fun currentTimeMillis() = wallClockMillis

                override fun elapsedRealtimeMillis() = testScheduler.currentTime
            },
            random = Random(7),
        )
        val points = async {
            source
                .observePoints(
                    initialPoint = SignalPoint(timestampMillis = wallClockMillis, value = 0.0),
                    expiresAtElapsedRealtimeMillis = 3_000L,
                )
                .take(2)
                .toList()
        }

        runCurrent()
        advanceTimeBy(1.seconds)
        runCurrent()
        wallClockMillis = 1_000L
        advanceTimeBy(1.seconds)
        runCurrent()

        assertEquals(listOf(11_000L, 12_000L), points.await().map { it.timestampMillis })
    }

    private fun testTimeProvider(currentTimeMillis: () -> Long) = object : TimeProvider {

        override fun currentTimeMillis() = currentTimeMillis()

        override fun elapsedRealtimeMillis() = currentTimeMillis()
    }
}

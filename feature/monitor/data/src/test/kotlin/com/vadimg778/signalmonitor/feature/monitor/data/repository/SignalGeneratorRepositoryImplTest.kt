package com.vadimg778.signalmonitor.feature.monitor.data.repository

import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.factory.SignalGeneratorFactory
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalColor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import com.vadimg778.signalmonitor.feature.monitor.domain.source.SignalPointSource
import com.vadimg778.signalmonitor.feature.monitor.domain.source.SignalPointSourceFactory
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class SignalGeneratorRepositoryImplTest {

    @Test
    fun `collects each source independently and completes every generator`() = runTest {
        val generatorFactory = SignalGeneratorFactory { count ->
            List(count) { index -> generator(index + 1) }
        }
        val pointSourceFactory = SignalPointSourceFactory { generatorId ->
            SignalPointSource { initialPoint, _ ->
                flowOf(
                    SignalPoint(
                        timestampMillis = 1_000L,
                        value = initialPoint.value + generatorId.value,
                    ),
                )
            }
        }
        val repository = SignalGeneratorRepositoryImpl(
            applicationScope = backgroundScope,
            generatorFactory = generatorFactory,
            pointSourceFactory = pointSourceFactory,
            timeProvider = testTimeProvider { testScheduler.currentTime },
            generatorCount = 3,
        )

        runCurrent()
        advanceTimeBy(100.milliseconds)
        runCurrent()
        val generators = repository.observeGenerators().first()

        assertEquals(3, generators.size)
        assertTrue(generators.all { it.status == GeneratorStatus.COMPLETED })
        assertEquals(listOf(1.0, 2.0, 3.0), generators.map { it.currentValue })
        assertTrue(generators.all { it.points.size == 2 })
    }

    @Test
    fun `updates remaining time even when a source emits no points`() = runTest {
        val generatorFactory = SignalGeneratorFactory {
            listOf(generator(id = 1, expiresAtElapsedRealtimeMillis = 2_000L))
        }
        val pointSourceFactory = SignalPointSourceFactory {
            SignalPointSource { _, _ -> flow { awaitCancellation() } }
        }
        val repository = SignalGeneratorRepositoryImpl(
            applicationScope = backgroundScope,
            generatorFactory = generatorFactory,
            pointSourceFactory = pointSourceFactory,
            timeProvider = testTimeProvider { testScheduler.currentTime },
            generatorCount = 1,
        )

        runCurrent()
        advanceTimeBy(1_100.milliseconds)
        runCurrent()
        val activeGenerator = repository.observeGenerators().first().single()
        assertTrue(activeGenerator.remainingTimeMillis in 1L..1_000L)
        assertEquals(GeneratorStatus.ACTIVE, activeGenerator.status)

        advanceTimeBy(1.seconds)
        runCurrent()
        val completedGenerator = repository.observeGenerators().first().single()
        assertEquals(0L, completedGenerator.remainingTimeMillis)
        assertEquals(GeneratorStatus.COMPLETED, completedGenerator.status)
    }

    @Test
    fun `preserves every point from ten burst sources`() = runTest {
        val generatorFactory = SignalGeneratorFactory { count ->
            List(count) { index ->
                generator(
                    id = index + 1,
                    expiresAtElapsedRealtimeMillis = Long.MAX_VALUE,
                )
            }
        }
        val pointSourceFactory = SignalPointSourceFactory { generatorId ->
            SignalPointSource { _, _ ->
                flow {
                    repeat(POINTS_PER_SOURCE) { index ->
                        emit(
                            SignalPoint(
                                timestampMillis = index.toLong() + 1L,
                                value = generatorId.value.toDouble(),
                            ),
                        )
                    }
                }
            }
        }
        val repository = SignalGeneratorRepositoryImpl(
            applicationScope = backgroundScope,
            generatorFactory = generatorFactory,
            pointSourceFactory = pointSourceFactory,
            timeProvider = testTimeProvider { testScheduler.currentTime },
            generatorCount = GENERATOR_COUNT,
        )

        runCurrent()
        advanceTimeBy(BURST_PROCESSING_TIMEOUT)
        runCurrent()
        val generators = repository.observeGenerators().first()

        assertEquals(GENERATOR_COUNT, generators.size)
        assertEquals(
            List(GENERATOR_COUNT) { POINTS_PER_SOURCE + 1 },
            generators.map { it.points.size },
        )
        assertTrue(generators.all { it.status == GeneratorStatus.COMPLETED })
    }

    private fun generator(id: Int, expiresAtElapsedRealtimeMillis: Long = 10_000L) =
        SignalGenerator(
            id = GeneratorId(id),
            name = "Generator #$id",
            color = SignalColor(id),
            expiresAtElapsedRealtimeMillis = expiresAtElapsedRealtimeMillis,
            remainingTimeMillis = expiresAtElapsedRealtimeMillis,
            status = GeneratorStatus.ACTIVE,
            points = persistentListOf(SignalPoint(timestampMillis = 0L, value = 0.0)),
        )

    private fun testTimeProvider(currentTimeMillis: () -> Long) = object : TimeProvider {

        override fun currentTimeMillis() = currentTimeMillis()

        override fun elapsedRealtimeMillis() = currentTimeMillis()
    }

    private companion object {

        const val GENERATOR_COUNT = 10
        const val POINTS_PER_SOURCE = 1_800
        val BURST_PROCESSING_TIMEOUT = 20.seconds
    }
}

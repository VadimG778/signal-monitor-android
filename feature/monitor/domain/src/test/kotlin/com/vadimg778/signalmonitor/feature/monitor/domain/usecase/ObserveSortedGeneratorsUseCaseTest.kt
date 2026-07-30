package com.vadimg778.signalmonitor.feature.monitor.domain.usecase

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalColor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import com.vadimg778.signalmonitor.feature.monitor.domain.repository.SignalGeneratorRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveSortedGeneratorsUseCaseTest {

    @Test
    fun `sorts generators by remaining lifetime then identifier`() = runTest {
        val repository = object : SignalGeneratorRepository {

            override fun observeGenerators(): Flow<List<SignalGenerator>> = flowOf(
                listOf(
                    generator(id = 3, remainingTimeMillis = 5_000L),
                    generator(id = 2, remainingTimeMillis = 1_000L),
                    generator(id = 1, remainingTimeMillis = 1_000L),
                ),
            )
        }

        val sorted = ObserveSortedGeneratorsUseCase(repository)().first()

        assertEquals(listOf(1, 2, 3), sorted.map { it.id.value })
    }

    private fun generator(id: Int, remainingTimeMillis: Long) = SignalGenerator(
        id = GeneratorId(id),
        name = "Generator #$id",
        color = SignalColor(id),
        expiresAtElapsedRealtimeMillis = remainingTimeMillis,
        remainingTimeMillis = remainingTimeMillis,
        status = GeneratorStatus.ACTIVE,
        points = persistentListOf(SignalPoint(timestampMillis = 0L, value = 0.0)),
    )
}

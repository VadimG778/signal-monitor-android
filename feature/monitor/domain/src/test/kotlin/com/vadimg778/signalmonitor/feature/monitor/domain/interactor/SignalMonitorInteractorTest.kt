package com.vadimg778.signalmonitor.feature.monitor.domain.interactor

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalColor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import com.vadimg778.signalmonitor.feature.monitor.domain.repository.SignalGeneratorRepository
import com.vadimg778.signalmonitor.feature.monitor.domain.usecase.ObserveSortedGeneratorsUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalMonitorInteractorTest {

    @Test
    fun `keeps visibility as monitor session state`() = runTest {
        val generator = generator(GeneratorId(1))
        val repository = object : SignalGeneratorRepository {

            override fun observeGenerators(): Flow<List<SignalGenerator>> = flowOf(
                listOf(generator),
            )
        }
        val interactor = SignalMonitorInteractor(ObserveSortedGeneratorsUseCase(repository))

        assertTrue(interactor.monitor.first().generators.single().isVisible)

        interactor.setGeneratorVisibility(generator.id, false)

        assertFalse(interactor.monitor.first().generators.single().isVisible)
    }

    private fun generator(id: GeneratorId) = SignalGenerator(
        id = id,
        name = "Generator #${id.value}",
        color = SignalColor(id.value),
        expiresAtElapsedRealtimeMillis = 1_000L,
        remainingTimeMillis = 1_000L,
        status = GeneratorStatus.ACTIVE,
        points = persistentListOf(SignalPoint(timestampMillis = 0L, value = 0.0)),
    )
}

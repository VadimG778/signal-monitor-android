package com.vadimg778.signalmonitor.feature.monitor.presentation

import com.vadimg778.signalmonitor.feature.monitor.domain.interactor.SignalMonitorInteractor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.MonitoredSignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalColor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalMonitor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import com.vadimg778.signalmonitor.feature.monitor.domain.repository.SignalGeneratorRepository
import com.vadimg778.signalmonitor.feature.monitor.domain.usecase.ObserveSortedGeneratorsUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.testWithInternalState

@OptIn(ExperimentalCoroutinesApi::class)
class SignalMonitorViewModelTest {

    private val mainDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(mainDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `maps state and handles visibility intent`() = runTest(mainDispatcher) {
        val generator = generator()
        val repository = TestSignalGeneratorRepository(listOf(generator))
        val interactor = SignalMonitorInteractor(ObserveSortedGeneratorsUseCase(repository))
        val viewModel = SignalMonitorViewModel(interactor)
        viewModel.testWithInternalState(this) {
            val monitorJob = runOnCreate()
            expectInternalState { expectedState(generator, isVisible = true) }

            containerHost.accept(
                SignalMonitorIntent.SetGeneratorVisibility(
                    generatorId = generator.id,
                    isVisible = false,
                ),
            )
            expectInternalState { expectedState(generator, isVisible = false) }

            monitorJob.cancel()
            cancelAndIgnoreRemainingItems()
        }
    }

    private fun expectedState(generator: SignalGenerator, isVisible: Boolean) =
        SignalMonitorUiMapper.map(
            SignalMonitor(
                generators = listOf(
                    MonitoredSignalGenerator(
                        generator = generator,
                        isVisible = isVisible,
                    ),
                ),
            ),
        )

    private fun generator() = SignalGenerator(
        id = GeneratorId(1),
        name = "Generator #1",
        color = SignalColor(0xFF123456.toInt()),
        expiresAtElapsedRealtimeMillis = 10_000L,
        remainingTimeMillis = 10_000L,
        status = GeneratorStatus.ACTIVE,
        points = persistentListOf(SignalPoint(timestampMillis = 0L, value = 0.0)),
    )

    private class TestSignalGeneratorRepository(initialValue: List<SignalGenerator>) :
        SignalGeneratorRepository {

        private val generators = MutableStateFlow(initialValue)

        override fun observeGenerators(): Flow<List<SignalGenerator>> = generators
    }
}

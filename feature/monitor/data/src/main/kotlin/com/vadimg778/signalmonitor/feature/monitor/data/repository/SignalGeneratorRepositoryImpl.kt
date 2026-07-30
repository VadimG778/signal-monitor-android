package com.vadimg778.signalmonitor.feature.monitor.data.repository

import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.factory.SignalGeneratorFactory
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import com.vadimg778.signalmonitor.feature.monitor.domain.repository.SignalGeneratorRepository
import com.vadimg778.signalmonitor.feature.monitor.domain.source.SignalPointSourceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class SignalGeneratorRepositoryImpl(
    generatorFactory: SignalGeneratorFactory,
    generatorCount: Int = DEFAULT_GENERATOR_COUNT,
    private val applicationScope: CoroutineScope,
    private val pointSourceFactory: SignalPointSourceFactory,
    private val timeProvider: TimeProvider,
) : SignalGeneratorRepository {

    private val generators = MutableStateFlow(generatorFactory.create(generatorCount))
    private val generatorUpdates = Channel<GeneratorUpdate>(Channel.BUFFERED)

    init {
        startUpdateProcessor()
        generators.value.forEach(::startGenerator)
        startCountdown()
    }

    override fun observeGenerators(): Flow<List<SignalGenerator>> = generators

    private fun startGenerator(generator: SignalGenerator) {
        applicationScope.launch {
            pointSourceFactory.create(generator.id)
                .observePoints(
                    initialPoint = generator.points.last(),
                    expiresAtElapsedRealtimeMillis = generator.expiresAtElapsedRealtimeMillis,
                )
                .collect { point ->
                    generatorUpdates.send(
                        GeneratorUpdate.Point(
                            generator.id,
                            point,
                        ),
                    )
                }

            generatorUpdates.send(GeneratorUpdate.Completed(generator.id))
        }
    }

    private fun startUpdateProcessor() {
        applicationScope.launch {
            while (true) {
                val firstUpdate = generatorUpdates.receive()
                delay(UPDATE_BATCH_WINDOW)
                val batch = buildList {
                    add(firstUpdate)
                    while (size < MAX_UPDATE_BATCH_SIZE) {
                        add(generatorUpdates.tryReceive().getOrNull() ?: break)
                    }
                }
                applyUpdates(batch)
            }
        }
    }

    private fun startCountdown() {
        applicationScope.launch {
            while (
                generators.value.any { generator ->
                    generator.status == GeneratorStatus.ACTIVE
                }
            ) {
                delay(COUNTDOWN_INTERVAL)
                generatorUpdates.send(GeneratorUpdate.CountdownTick)
            }
        }
    }

    private fun applyUpdates(batch: List<GeneratorUpdate>) {
        val pointsByGenerator = mutableMapOf<GeneratorId, MutableList<SignalPoint>>()
        val completedGeneratorIds = mutableSetOf<GeneratorId>()
        batch.forEach { update ->
            when (update) {
                is GeneratorUpdate.Point ->
                    pointsByGenerator.getOrPut(update.generatorId, ::mutableListOf)
                        .add(update.point)

                is GeneratorUpdate.Completed -> completedGeneratorIds.add(update.generatorId)

                GeneratorUpdate.CountdownTick -> Unit
            }
        }
        val elapsedRealtimeMillis = timeProvider.elapsedRealtimeMillis()
        generators.update { currentGenerators ->
            currentGenerators.map { generator ->
                val points = pointsByGenerator[generator.id]
                val updatedGenerator = if (points.isNullOrEmpty()) {
                    generator
                } else {
                    generator.append(points)
                }
                when {
                    generator.id in completedGeneratorIds -> updatedGenerator.complete()

                    updatedGenerator.status == GeneratorStatus.ACTIVE ->
                        updatedGenerator.updateRemainingTime(elapsedRealtimeMillis)

                    else -> updatedGenerator
                }
            }
        }
    }

    private sealed interface GeneratorUpdate {

        data class Point(val generatorId: GeneratorId, val point: SignalPoint) : GeneratorUpdate

        data class Completed(val generatorId: GeneratorId) : GeneratorUpdate

        data object CountdownTick : GeneratorUpdate
    }

    private companion object {
        const val DEFAULT_GENERATOR_COUNT = 10
        val COUNTDOWN_INTERVAL = 1.seconds
        val UPDATE_BATCH_WINDOW = 32.milliseconds
        const val MAX_UPDATE_BATCH_SIZE = 512
    }
}

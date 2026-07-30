package com.vadimg778.signalmonitor.feature.monitor.data.factory

import com.vadimg778.signalmonitor.feature.monitor.data.source.MAX_RANDOM_WALK_VALUE
import com.vadimg778.signalmonitor.feature.monitor.data.source.MIN_RANDOM_WALK_VALUE
import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.factory.SignalGeneratorFactory
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalColor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import kotlinx.collections.immutable.persistentListOf
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SignalGeneratorFactoryImpl(
    private val timeProvider: TimeProvider,
    private val random: Random = Random.Default,
) : SignalGeneratorFactory {

    override fun create(count: Int): List<SignalGenerator> {
        require(count in 1..SignalColorPalette.size)

        val startedAtMillis = timeProvider.currentTimeMillis()
        val startedAtElapsedRealtimeMillis = timeProvider.elapsedRealtimeMillis()

        return List(count) { index ->
            val lifetime = random.nextLong(
                MIN_LIFETIME.inWholeSeconds,
                MAX_LIFETIME_EXCLUSIVE.inWholeSeconds,
            ).seconds

            val initialValue = random.nextDouble(MIN_RANDOM_WALK_VALUE, MAX_RANDOM_WALK_VALUE)

            SignalGenerator(
                id = GeneratorId(index + 1),
                name = "Generator #${index + 1}",
                color = SignalColor(SignalColorPalette[index]),
                expiresAtElapsedRealtimeMillis = startedAtElapsedRealtimeMillis +
                    lifetime.inWholeMilliseconds,
                remainingTimeMillis = lifetime.inWholeMilliseconds,
                status = GeneratorStatus.ACTIVE,
                points = persistentListOf(SignalPoint(startedAtMillis, initialValue)),
            )
        }
    }

    private companion object {

        val MIN_LIFETIME = 1.minutes
        val MAX_LIFETIME_EXCLUSIVE = 30.minutes
    }
}

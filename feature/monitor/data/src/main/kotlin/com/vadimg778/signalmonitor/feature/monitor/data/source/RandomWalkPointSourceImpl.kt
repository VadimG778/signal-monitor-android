package com.vadimg778.signalmonitor.feature.monitor.data.source

import com.vadimg778.signalmonitor.feature.monitor.data.time.TimeProvider
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import com.vadimg778.signalmonitor.feature.monitor.domain.source.SignalPointSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds

class RandomWalkPointSourceImpl(
    private val timeProvider: TimeProvider,
    private val random: Random,
) : SignalPointSource {

    override fun observePoints(
        initialPoint: SignalPoint,
        expiresAtElapsedRealtimeMillis: Long,
    ): Flow<SignalPoint> = flow {
        val startedAtElapsedRealtimeMillis = timeProvider.elapsedRealtimeMillis()
        var previousValue = initialPoint.value
        while (true) {
            delay(POINT_INTERVAL)
            val elapsedRealtimeMillis = timeProvider.elapsedRealtimeMillis()
            if (elapsedRealtimeMillis >= expiresAtElapsedRealtimeMillis) break

            previousValue += random.nextDouble(MIN_RANDOM_WALK_VALUE, MAX_RANDOM_WALK_VALUE)
            emit(
                SignalPoint(
                    timestampMillis = initialPoint.timestampMillis +
                        elapsedRealtimeMillis -
                        startedAtElapsedRealtimeMillis,
                    value = previousValue,
                ),
            )
        }
    }

    private companion object {

        val POINT_INTERVAL = 1.seconds
    }
}

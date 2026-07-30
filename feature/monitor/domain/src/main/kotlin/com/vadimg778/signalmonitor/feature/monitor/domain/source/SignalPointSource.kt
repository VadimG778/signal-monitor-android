package com.vadimg778.signalmonitor.feature.monitor.domain.source

import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import kotlinx.coroutines.flow.Flow

fun interface SignalPointSource {

    fun observePoints(
        initialPoint: SignalPoint,
        expiresAtElapsedRealtimeMillis: Long,
    ): Flow<SignalPoint>
}

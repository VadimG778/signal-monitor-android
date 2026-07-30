package com.vadimg778.signalmonitor.feature.monitor.domain.model

import kotlinx.collections.immutable.PersistentList

data class SignalGenerator(
    val id: GeneratorId,
    val name: String,
    val color: SignalColor,
    val expiresAtElapsedRealtimeMillis: Long,
    val remainingTimeMillis: Long,
    val status: GeneratorStatus,
    val points: PersistentList<SignalPoint>,
) {

    init {
        require(points.isNotEmpty())
        require(remainingTimeMillis >= 0L)
        require(
            (status == GeneratorStatus.ACTIVE && remainingTimeMillis > 0L) ||
                (status == GeneratorStatus.COMPLETED && remainingTimeMillis == 0L),
        )
    }

    val currentValue: Double
        get() = points.last().value

    fun append(points: Collection<SignalPoint>): SignalGenerator = copy(
        points = this.points.addingAll(points),
    )

    fun updateRemainingTime(elapsedRealtimeMillis: Long): SignalGenerator {
        val remainingTimeMillis = (
            expiresAtElapsedRealtimeMillis - elapsedRealtimeMillis
            ).coerceAtLeast(0L)

        return copy(
            remainingTimeMillis = remainingTimeMillis,
            status = if (remainingTimeMillis == 0L) {
                GeneratorStatus.COMPLETED
            } else {
                GeneratorStatus.ACTIVE
            },
        )
    }

    fun complete(): SignalGenerator = copy(
        remainingTimeMillis = 0L,
        status = GeneratorStatus.COMPLETED,
    )
}

package com.vadimg778.signalmonitor.feature.monitor.ui.chart

internal object XRangeAutoFollowPolicy {

    fun initialRange(latestTimestampMillis: Long, durationMillis: Long): LongRange = anchoredRange(
        latestTimestampMillis,
        durationMillis,
    )

    fun follow(visibleRange: LongRange, latestTimestampMillis: Long): LongRange {
        val durationMillis = visibleRange.last - visibleRange.first
        val rightPaddingMillis = (durationMillis * RIGHT_PADDING_FRACTION).toLong()
        if (latestTimestampMillis < visibleRange.last - rightPaddingMillis) return visibleRange

        return anchoredRange(latestTimestampMillis, durationMillis)
    }

    fun isAtLatest(visibleRange: LongRange, latestTimestampMillis: Long): Boolean {
        val durationMillis = visibleRange.last - visibleRange.first
        val rightPaddingMillis = (durationMillis * RIGHT_PADDING_FRACTION).toLong()
        return latestTimestampMillis in (visibleRange.last - rightPaddingMillis)..visibleRange.last
    }

    private fun anchoredRange(latestTimestampMillis: Long, durationMillis: Long): LongRange {
        val endMillis = latestTimestampMillis + (durationMillis * RIGHT_PADDING_FRACTION).toLong()
        return (endMillis - durationMillis)..endMillis
    }

    private const val RIGHT_PADDING_FRACTION = 0.1
}

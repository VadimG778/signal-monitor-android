package com.vadimg778.signalmonitor.feature.monitor.ui.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class XRangeAutoFollowPolicyTest {

    @Test
    fun `places the first value near the right edge`() {
        val initialRange = XRangeAutoFollowPolicy.initialRange(
            latestTimestampMillis = 60_000L,
            durationMillis = 60_000L,
        )

        assertEquals(6_000L..66_000L, initialRange)
    }

    @Test
    fun `keeps the range while latest value is before follow margin`() {
        val visibleRange = 0L..60_000L

        val followedRange = XRangeAutoFollowPolicy.follow(visibleRange, 53_000L)

        assertEquals(visibleRange, followedRange)
    }

    @Test
    fun `moves the range while preserving user zoom`() {
        val visibleRange = 40_000L..60_000L

        val followedRange = XRangeAutoFollowPolicy.follow(visibleRange, 59_000L)

        assertEquals(41_000L..61_000L, followedRange)
    }

    @Test
    fun `recognizes when a manually selected range returns to live data`() {
        assertTrue(
            XRangeAutoFollowPolicy.isAtLatest(
                visibleRange = 0L..60_000L,
                latestTimestampMillis = 54_000L,
            ),
        )
        assertFalse(
            XRangeAutoFollowPolicy.isAtLatest(
                visibleRange = 0L..60_000L,
                latestTimestampMillis = 53_999L,
            ),
        )
    }
}

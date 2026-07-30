package com.vadimg778.signalmonitor.feature.monitor.presentation

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorStatus
import com.vadimg778.signalmonitor.feature.monitor.domain.model.MonitoredSignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalColor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalMonitor
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalMonitorUiMapperTest {

    @Test
    fun `maps generator state to list and chart models`() {
        val points = persistentListOf(
            SignalPoint(timestampMillis = 1_000L, value = 1.5),
            SignalPoint(timestampMillis = 2_000L, value = 2.5),
        )
        val generator = SignalGenerator(
            id = GeneratorId(3),
            name = "Generator #3",
            color = SignalColor(0xFF123456.toInt()),
            expiresAtElapsedRealtimeMillis = 9_000L,
            remainingTimeMillis = 0L,
            status = GeneratorStatus.COMPLETED,
            points = points,
        )

        val state = SignalMonitorUiMapper.map(
            SignalMonitor(
                generators = listOf(
                    MonitoredSignalGenerator(
                        generator = generator,
                        isVisible = false,
                    ),
                ),
            ),
        )

        val item = state.generators.single()
        assertEquals(generator.id, item.id)
        assertEquals(generator.name, item.name)
        assertEquals(generator.color.argb, item.colorArgb)
        assertEquals(generator.remainingTimeMillis, item.remainingTimeMillis)
        assertEquals(generator.currentValue, item.currentValue, 0.0)
        assertFalse(item.isVisible)
        assertTrue(item.isCompleted)

        val series = state.series.single()
        assertEquals(generator.id, series.id)
        assertEquals(generator.name, series.name)
        assertEquals(generator.color.argb, series.colorArgb)
        assertFalse(series.isVisible)
        assertFalse(series.isActive)
        assertSame(points, series.points)
    }
}

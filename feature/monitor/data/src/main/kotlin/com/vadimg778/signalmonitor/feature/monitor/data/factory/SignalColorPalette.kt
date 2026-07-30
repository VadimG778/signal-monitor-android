package com.vadimg778.signalmonitor.feature.monitor.data.factory

internal object SignalColorPalette {

    private val colors = intArrayOf(
        0xFFE53935.toInt(),
        0xFF1E88E5.toInt(),
        0xFF43A047.toInt(),
        0xFF8E24AA.toInt(),
        0xFFFB8C00.toInt(),
        0xFF00ACC1.toInt(),
        0xFFD81B60.toInt(),
        0xFF3949AB.toInt(),
        0xFF7CB342.toInt(),
        0xFF6D4C41.toInt(),
    )

    val size: Int
        get() = colors.size

    operator fun get(index: Int): Int = colors[index]
}

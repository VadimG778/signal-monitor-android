package com.vadimg778.signalmonitor.feature.monitor.presentation

import java.util.Locale

internal fun formatSignalValue(value: Double): String = String.format(Locale.US, "%.2f", value)

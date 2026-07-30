package com.vadimg778.signalmonitor.feature.monitor.domain.factory

import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator

fun interface SignalGeneratorFactory {

    fun create(count: Int): List<SignalGenerator>
}

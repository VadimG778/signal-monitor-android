package com.vadimg778.signalmonitor.feature.monitor.domain.source

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId

fun interface SignalPointSourceFactory {

    fun create(generatorId: GeneratorId): SignalPointSource
}

package com.vadimg778.signalmonitor.feature.monitor.presentation

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId

sealed interface SignalMonitorIntent {

    data class SetGeneratorVisibility(val generatorId: GeneratorId, val isVisible: Boolean) :
        SignalMonitorIntent
}

package com.vadimg778.signalmonitor.feature.monitor.ui

import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId

object SignalMonitorTestTags {

    const val GENERATOR_LIST = "generator_list"
    const val SIGNAL_LEGEND = "signal_legend"

    fun generatorItem(generatorId: GeneratorId) = "generator_item_${generatorId.value}"

    fun generatorCheckbox(generatorId: GeneratorId) = "generator_checkbox_${generatorId.value}"

    fun generatorTimer(generatorId: GeneratorId) = "generator_timer_${generatorId.value}"
}

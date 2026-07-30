package com.vadimg778.signalmonitor.feature.monitor.presentation

import androidx.compose.runtime.Immutable
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import kotlinx.collections.immutable.PersistentList

@Immutable
data class SignalSeriesUiModel(
    val id: GeneratorId,
    val name: String,
    val colorArgb: Int,
    val isVisible: Boolean,
    val isActive: Boolean,
    val points: PersistentList<SignalPoint>,
)

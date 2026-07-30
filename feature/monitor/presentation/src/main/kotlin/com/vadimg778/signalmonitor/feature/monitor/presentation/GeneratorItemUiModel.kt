package com.vadimg778.signalmonitor.feature.monitor.presentation

import androidx.compose.runtime.Immutable
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId

@Immutable
data class GeneratorItemUiModel(
    val id: GeneratorId,
    val name: String,
    val colorArgb: Int,
    val remainingTimeMillis: Long,
    val currentValue: Double,
    val isVisible: Boolean,
    val isCompleted: Boolean,
)

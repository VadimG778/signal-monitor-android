package com.vadimg778.signalmonitor.feature.monitor.presentation

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class SignalMonitorUiState(
    val generators: PersistentList<GeneratorItemUiModel> = persistentListOf(),
    val series: PersistentList<SignalSeriesUiModel> = persistentListOf(),
)

package com.vadimg778.signalmonitor.feature.monitor.domain.repository

import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalGenerator
import kotlinx.coroutines.flow.Flow

interface SignalGeneratorRepository {

    fun observeGenerators(): Flow<List<SignalGenerator>>
}

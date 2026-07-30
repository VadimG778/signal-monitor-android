package com.vadimg778.signalmonitor.feature.monitor.presentation

import androidx.lifecycle.ViewModel
import com.vadimg778.signalmonitor.feature.monitor.domain.interactor.SignalMonitorInteractor
import org.orbitmvi.orbit.OrbitContainerHost
import org.orbitmvi.orbit.viewmodel.orbitContainer

class SignalMonitorViewModel(private val interactor: SignalMonitorInteractor) :
    ViewModel(),
    OrbitContainerHost<SignalMonitorUiState, SignalMonitorUiState, Nothing> {

    override val container = orbitContainer<SignalMonitorUiState, Nothing>(SignalMonitorUiState()) {
        repeatOnSubscription {
            interactor.monitor.collect { monitor ->
                reduce { SignalMonitorUiMapper.map(monitor) }
            }
        }
    }

    fun accept(intent: SignalMonitorIntent) = intent {
        when (intent) {
            is SignalMonitorIntent.SetGeneratorVisibility ->
                interactor.setGeneratorVisibility(intent.generatorId, intent.isVisible)
        }
    }
}

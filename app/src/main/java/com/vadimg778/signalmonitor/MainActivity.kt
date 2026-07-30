package com.vadimg778.signalmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.vadimg778.signalmonitor.core.designsystem.theme.SignalMonitorTheme
import com.vadimg778.signalmonitor.feature.monitor.navigation.SignalMonitorDestination
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalMonitorViewModel
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorRoute
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SignalMonitorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val backStack = rememberNavBackStack(SignalMonitorDestination)
                    NavDisplay(
                        backStack = backStack,
                        onBack = backStack::removeLastOrNull,
                        entryProvider = entryProvider {
                            entry<SignalMonitorDestination> {
                                SignalMonitorRoute(
                                    viewModel = koinViewModel<SignalMonitorViewModel>(),
                                    modifier = Modifier.safeDrawingPadding(),
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

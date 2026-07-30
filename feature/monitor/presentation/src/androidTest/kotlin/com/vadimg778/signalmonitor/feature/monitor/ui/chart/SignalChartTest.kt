package com.vadimg778.signalmonitor.feature.monitor.ui.chart

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scichart.charting.visuals.SciChartSurface
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.domain.model.SignalPoint
import com.vadimg778.signalmonitor.feature.monitor.presentation.R
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalSeriesUiModel
import kotlinx.collections.immutable.toPersistentList
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignalChartTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUpLicense() {
        val licenseKey = composeRule.activity.getString(R.string.scichart_license_key)
        if (licenseKey.isNotBlank()) {
            SciChartSurface.setRuntimeLicenseKey(licenseKey)
        }
    }

    @Test
    fun rendersTenCompletePointHistories() {
        val models = List(GENERATOR_COUNT) { generatorIndex ->
            SignalSeriesUiModel(
                id = GeneratorId(generatorIndex + 1),
                name = "Generator #${generatorIndex + 1}",
                colorArgb = 0xFF000000.toInt() or (generatorIndex + 1) * 0x00111111,
                isVisible = true,
                isActive = true,
                points = List(POINTS_PER_GENERATOR) { pointIndex ->
                    SignalPoint(
                        timestampMillis = pointIndex * 1_000L,
                        value = (pointIndex % 100 - 50).toDouble(),
                    )
                }.toPersistentList(),
            )
        }.toPersistentList()

        composeRule.setContent {
            SignalChart(
                series = models,
                modifier = Modifier.fillMaxSize(),
            )
        }

        composeRule.waitUntil(timeoutMillis = 10_000L) {
            val surface = composeRule.activity.window.decorView.findSciChartSurface()
            surface != null &&
                surface.renderableSeries.size == GENERATOR_COUNT &&
                (0 until GENERATOR_COUNT).all { index ->
                    surface.renderableSeries[index].dataSeries.count == POINTS_PER_GENERATOR
                }
        }
    }

    private fun View.findSciChartSurface(): SciChartSurface? = when (this) {
        is SciChartSurface -> this

        is ViewGroup ->
            (0 until childCount).firstNotNullOfOrNull { index ->
                getChildAt(index).findSciChartSurface()
            }

        else -> null
    }

    private companion object {

        const val GENERATOR_COUNT = 10
        const val POINTS_PER_GENERATOR = 1_800
    }
}

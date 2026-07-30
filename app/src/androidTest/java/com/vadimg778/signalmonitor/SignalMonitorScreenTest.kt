package com.vadimg778.signalmonitor

import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.data.model.DateRange
import com.scichart.data.model.DoubleRange
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.GENERATOR_LIST
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.SIGNAL_LEGEND
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.generatorCheckbox
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.generatorItem
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.generatorTimer
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
class SignalMonitorScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun displaysAllTenGenerators() {
        waitForGenerators()
        composeRule.onNodeWithText("Signal Monitor").assertIsDisplayed()
        val generatorListNode = composeRule.onNodeWithTag(GENERATOR_LIST)

        (1..10).forEach { value ->
            val generatorId = GeneratorId(value)
            generatorListNode.performScrollToNode(hasTestTag(generatorItem(generatorId)))
            composeRule.onNodeWithTag(generatorItem(generatorId)).assertIsDisplayed()
        }
    }

    @Test
    fun checkboxUpdatesLineVisibilityAndLegend() {
        waitForGenerators()
        val generatorId = GeneratorId(1)
        val generatorName = "Generator #${generatorId.value}"
        val legendItem = hasText(generatorName) and hasAnyAncestor(hasTestTag(SIGNAL_LEGEND))
        composeRule
            .onNodeWithTag(SIGNAL_LEGEND)
            .performScrollToNode(hasText(generatorName))
        composeRule.onNode(legendItem).assertIsDisplayed()

        composeRule
            .onNodeWithTag(GENERATOR_LIST)
            .performScrollToNode(hasTestTag(generatorItem(generatorId)))
        composeRule.onNodeWithTag(generatorCheckbox(generatorId)).performClick()

        composeRule.waitUntil(timeoutMillis = 1_000L) {
            composeRule.onAllNodes(legendItem).fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithTag(generatorCheckbox(generatorId)).assertIsOff()
        composeRule.runOnIdle {
            val surface = composeRule.activity.requireSciChartSurface()
            val renderableSeries = (0 until surface.renderableSeries.size)
                .map(surface.renderableSeries::get)
                .single { series -> series.dataSeries.seriesName == generatorName }
            assertFalse(renderableSeries.isVisible)
            assertTrue(
                (0 until surface.annotations.size)
                    .map(surface.annotations::get)
                    .count { annotation -> !annotation.isHidden } == 9,
            )
        }
    }

    @Test
    fun yRangeContainsVisibleValuesInsideCurrentTimeRange() {
        composeRule.waitUntil(timeoutMillis = 2_500L) {
            val series = composeRule.activity.requireSciChartSurface().renderableSeries
            series.size > 0 && series[0].dataSeries.count > 1
        }

        composeRule.runOnIdle {
            val surface = composeRule.activity.requireSciChartSurface()
            val xRange = surface.xAxes[0].visibleRange as DateRange
            val yRange = surface.yAxes[0].visibleRange as DoubleRange
            val visibleValues = (0 until surface.renderableSeries.size)
                .asSequence()
                .map(surface.renderableSeries::get)
                .filter { series -> series.isVisible }
                .flatMap { series ->
                    val dataSeries = series.dataSeries as XyDataSeries<*, *>
                    (0 until dataSeries.count)
                        .asSequence()
                        .filter { index ->
                            (dataSeries.xValues[index] as Date) in xRange.min..xRange.max
                        }
                        .map { index -> dataSeries.yValues[index] as Double }
                }
                .toList()

            assertTrue(visibleValues.isNotEmpty())
            assertTrue(yRange.min <= visibleValues.min())
            assertTrue(yRange.max >= visibleValues.max())
        }
    }

    @Test
    fun timerUpdatesWhileScreenIsVisible() {
        waitForGenerators()
        val generatorId = GeneratorId(1)
        composeRule
            .onNodeWithTag(GENERATOR_LIST)
            .performScrollToNode(hasTestTag(generatorItem(generatorId)))
        val timer = composeRule.onNodeWithTag(generatorTimer(generatorId))
        val initialValue = timer.fetchSemanticsNode().config[SemanticsProperties.Text]

        composeRule.waitUntil(timeoutMillis = 2_500L) {
            timer.fetchSemanticsNode().config[SemanticsProperties.Text] != initialValue
        }
    }

    @Test
    fun generatorsContinueWhileActivityIsInBackground() {
        waitForGenerators()
        val generatorId = GeneratorId(1)
        composeRule
            .onNodeWithTag(GENERATOR_LIST)
            .performScrollToNode(hasTestTag(generatorItem(generatorId)))
        val initialValue = composeRule
            .onNodeWithTag(generatorTimer(generatorId))
            .fetchSemanticsNode()
            .config[SemanticsProperties.Text]

        composeRule.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        SystemClock.sleep(2_200L)
        composeRule.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)

        composeRule
            .onNodeWithTag(GENERATOR_LIST)
            .performScrollToNode(hasTestTag(generatorItem(generatorId)))
        composeRule.waitUntil(timeoutMillis = 1_000L) {
            composeRule
                .onNodeWithTag(generatorTimer(generatorId))
                .fetchSemanticsNode()
                .config[SemanticsProperties.Text] != initialValue
        }
    }

    private fun waitForGenerators() {
        composeRule.waitUntil(timeoutMillis = 5_000L) {
            composeRule
                .onAllNodes(hasTestTag(generatorItem(GeneratorId(1))))
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    private fun MainActivity.requireSciChartSurface(): SciChartSurface = checkNotNull(
        window.decorView.findSciChartSurface(),
    )

    private fun View.findSciChartSurface(): SciChartSurface? = when (this) {
        is SciChartSurface -> this

        is ViewGroup ->
            (0 until childCount).firstNotNullOfOrNull { index ->
                getChildAt(index).findSciChartSurface()
            }

        else -> null
    }
}

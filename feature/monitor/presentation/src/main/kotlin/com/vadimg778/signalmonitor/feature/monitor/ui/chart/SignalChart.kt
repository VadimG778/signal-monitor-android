package com.vadimg778.signalmonitor.feature.monitor.ui.chart

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.scichart.charting.ClipMode
import com.scichart.charting.Direction2D
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.PinchZoomModifier
import com.scichart.charting.modifiers.ZoomPanModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.annotations.AnnotationLabel
import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation
import com.scichart.charting.visuals.annotations.LabelPlacement
import com.scichart.charting.visuals.axes.AutoRange
import com.scichart.charting.visuals.axes.DateAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.model.DateValues
import com.scichart.core.model.DoubleValues
import com.scichart.data.model.DateRange
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.common.SolidPenStyle
import com.vadimg778.signalmonitor.core.designsystem.theme.SignalMonitorColors
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.presentation.R
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalSeriesUiModel
import com.vadimg778.signalmonitor.feature.monitor.presentation.formatSignalValue
import kotlinx.collections.immutable.PersistentList
import java.util.Date
import kotlin.time.Duration.Companion.minutes

@Composable
internal fun SignalChart(
    series: PersistentList<SignalSeriesUiModel>,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainer))
        return
    }

    val context = LocalContext.current
    val markerContentColorArgb = SignalMonitorColors.signalValueLabel.toArgb()
    val controller = remember(context, markerContentColorArgb) {
        SignalChartController(context, markerContentColorArgb)
    }
    AndroidView(
        factory = { controller.surface },
        modifier = modifier,
        update = { controller.render(series) },
    )
}

@SuppressLint("RtlHardcoded")
private class SignalChartController(
    private val context: Context,
    private val markerContentColorArgb: Int,
) {

    val surface = SciChartSurface(context)

    private val xAxis = DateAxis(context).apply {
        axisTitle = context.getString(R.string.axis_time)
        autoRange = AutoRange.Never
        textFormatting = TIME_AXIS_LABEL_FORMAT
        subDayTextFormatting = TIME_AXIS_LABEL_FORMAT
    }

    private val yAxis = NumericAxis(context).apply {
        axisTitle = context.getString(R.string.axis_value)
        autoRange = AutoRange.Never
        growBy = DoubleRange(Y_GROW_BY, Y_GROW_BY)
        textFormatting = VALUE_AXIS_LABEL_FORMAT
    }

    private val chartSeries = mutableMapOf<GeneratorId, ChartSeries>()
    private var isInitialXRangeSet = false
    private var isAutoFollowEnabled = true
    private var isUpdatingXRange = false
    private var latestTimestampMillis: Long? = null

    init {
        UpdateSuspender.using(surface) {
            surface.xAxes.add(xAxis)
            surface.yAxes.add(yAxis)
            surface.chartModifiers.add(
                object : ZoomPanModifier() {

                    override fun onScroll(
                        initialEvent: MotionEvent?,
                        currentEvent: MotionEvent,
                        distanceX: Float,
                        distanceY: Float,
                    ): Boolean {
                        isAutoFollowEnabled = false
                        return super.onScroll(initialEvent, currentEvent, distanceX, distanceY)
                    }
                }.apply {
                    direction = Direction2D.XDirection
                    clipModeX = ClipMode.None
                },
            )
            surface.chartModifiers.add(
                object : PinchZoomModifier() {

                    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                        isAutoFollowEnabled = false
                        return super.onScaleBegin(detector)
                    }
                }.apply {
                    direction = Direction2D.XDirection
                },
            )
        }
        xAxis.setVisibleRangeChangeListener { _, _, _, _ ->
            if (isInitialXRangeSet && !isUpdatingXRange) {
                surface.zoomExtentsY()
                isAutoFollowEnabled = latestTimestampMillis?.let { latestTimestampMillis ->
                    XRangeAutoFollowPolicy.isAtLatest(
                        visibleRange = (xAxis.visibleRange as DateRange).toLongRange(),
                        latestTimestampMillis = latestTimestampMillis,
                    )
                } ?: false
            }
        }
    }

    fun render(models: List<SignalSeriesUiModel>) {
        latestTimestampMillis = models.asSequence()
            .filter(SignalSeriesUiModel::isVisible)
            .mapNotNull { model -> model.points.lastOrNull()?.timestampMillis }
            .maxOrNull()
        UpdateSuspender.using(surface) {
            models.forEach(::renderSeries)
            setInitialXRange(models)
            followLatestXValue()
            if (models.any(SignalSeriesUiModel::isVisible)) {
                surface.zoomExtentsY()
            }
        }
    }

    private fun renderSeries(model: SignalSeriesUiModel) {
        val current = chartSeries.getOrPut(model.id) { createChartSeries(model) }
        appendPoints(
            dataSeries = current.dataSeries,
            model = model,
            fromIndex = current.appendedPointCount,
        )
        current.appendedPointCount = model.points.size
        current.renderableSeries.isVisible = model.isVisible

        val lastPoint = model.points.last()
        current.lastValueAnnotation.apply {
            x1 = Date(lastPoint.timestampMillis)
            y1 = lastPoint.value
            labelValue = formatSignalValue(lastPoint.value)
            setIsHidden(!model.isVisible || !model.isActive)
        }
    }

    private fun createChartSeries(model: SignalSeriesUiModel): ChartSeries {
        val dataSeries = XyDataSeries(Date::class.java, Double::class.javaObjectType).apply {
            seriesName = model.name
        }
        val stroke = SolidPenStyle(model.colorArgb, true, LINE_THICKNESS, null)
        val renderableSeries = FastLineRenderableSeries().apply {
            this.dataSeries = dataSeries
            strokeStyle = stroke
        }
        val label = AnnotationLabel(context).apply {
            labelPlacement = LabelPlacement.Axis
            fontStyle = FontStyle(
                context.resources.getDimension(R.dimen.axis_marker_text_size),
                markerContentColorArgb,
            )
            setBackgroundColor(model.colorArgb)
        }
        val annotation = HorizontalLineAnnotation(context).apply {
            horizontalGravity = Gravity.RIGHT
            this.stroke = stroke
            setIsEditable(false)
            annotationLabels.add(label)
        }

        surface.renderableSeries.add(renderableSeries)
        surface.annotations.add(annotation)
        renderableSeries.isVisible = model.isVisible
        return ChartSeries(
            dataSeries = dataSeries,
            renderableSeries = renderableSeries,
            lastValueAnnotation = annotation,
        )
    }

    private fun appendPoints(
        dataSeries: XyDataSeries<Date, Double>,
        model: SignalSeriesUiModel,
        fromIndex: Int,
    ) {
        val pointCount = model.points.size - fromIndex
        if (pointCount == 0) return

        val xValues = DateValues(pointCount)
        val yValues = DoubleValues(pointCount)
        for (index in fromIndex until model.points.size) {
            val point = model.points[index]
            xValues.addTime(point.timestampMillis)
            yValues.add(point.value)
        }
        dataSeries.append(xValues, yValues)
    }

    private fun setInitialXRange(models: List<SignalSeriesUiModel>) {
        if (isInitialXRangeSet) return

        val firstTimestampMillis = models.asSequence()
            .mapNotNull { model -> model.points.firstOrNull()?.timestampMillis }
            .minOrNull()
            ?: return
        val initialRange = XRangeAutoFollowPolicy.initialRange(
            latestTimestampMillis = firstTimestampMillis,
            durationMillis = INITIAL_VISIBLE_DURATION.inWholeMilliseconds,
        )
        setVisibleXRange(initialRange.first, initialRange.last)
        isInitialXRangeSet = true
    }

    private fun followLatestXValue() {
        latestTimestampMillis?.let { latestTimestampMillis ->
            if (isInitialXRangeSet && isAutoFollowEnabled) {
                val visibleRange = (xAxis.visibleRange as DateRange).toLongRange()
                val followedRange = XRangeAutoFollowPolicy.follow(
                    visibleRange,
                    latestTimestampMillis,
                )
                if (followedRange != visibleRange) {
                    setVisibleXRange(
                        minMillis = followedRange.first,
                        maxMillis = followedRange.last,
                    )
                }
            }
        }
    }

    private fun setVisibleXRange(minMillis: Long, maxMillis: Long) {
        isUpdatingXRange = true
        xAxis.visibleRange = DateRange(Date(minMillis), Date(maxMillis))
        isUpdatingXRange = false
    }

    private fun DateRange.toLongRange(): LongRange = min.time..max.time

    private data class ChartSeries(
        val dataSeries: XyDataSeries<Date, Double>,
        val renderableSeries: FastLineRenderableSeries,
        val lastValueAnnotation: HorizontalLineAnnotation,
        var appendedPointCount: Int = 0,
    )

    private companion object {

        val INITIAL_VISIBLE_DURATION = 1.minutes
        const val LINE_THICKNESS = 2f
        const val TIME_AXIS_LABEL_FORMAT = "HH:mm:ss"
        const val VALUE_AXIS_LABEL_FORMAT = "0.00"
        const val Y_GROW_BY = 0.1
    }
}

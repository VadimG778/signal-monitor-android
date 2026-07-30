package com.vadimg778.signalmonitor.feature.monitor.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vadimg778.signalmonitor.core.designsystem.dimension.SignalMonitorSpacing
import com.vadimg778.signalmonitor.core.designsystem.theme.SignalMonitorTheme
import com.vadimg778.signalmonitor.feature.monitor.domain.model.GeneratorId
import com.vadimg778.signalmonitor.feature.monitor.presentation.GeneratorItemUiModel
import com.vadimg778.signalmonitor.feature.monitor.presentation.R
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalMonitorIntent
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalMonitorUiState
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalMonitorViewModel
import com.vadimg778.signalmonitor.feature.monitor.presentation.SignalSeriesUiModel
import com.vadimg778.signalmonitor.feature.monitor.presentation.formatSignalValue
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.GENERATOR_LIST
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.SIGNAL_LEGEND
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.generatorCheckbox
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.generatorItem
import com.vadimg778.signalmonitor.feature.monitor.ui.SignalMonitorTestTags.generatorTimer
import com.vadimg778.signalmonitor.feature.monitor.ui.chart.SignalChart
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import org.orbitmvi.orbit.compose.collectAsState
import java.util.Locale

@Composable
fun SignalMonitorRoute(viewModel: SignalMonitorViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.collectAsState()
    SignalMonitorScreen(
        state = state,
        onIntent = viewModel::accept,
        modifier = modifier,
    )
}

@Composable
internal fun SignalMonitorScreen(
    state: SignalMonitorUiState,
    onIntent: (SignalMonitorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val onVisibilityChanged: (GeneratorId, Boolean) -> Unit = { generatorId, isVisible ->
        onIntent(
            SignalMonitorIntent.SetGeneratorVisibility(
                generatorId = generatorId,
                isVisible = isVisible,
            ),
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.screen_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                horizontal = SignalMonitorSpacing.extraLarge,
                vertical = SignalMonitorSpacing.large,
            ),
        )
        HorizontalDivider()
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            if (maxWidth >= EXPANDED_LAYOUT_MIN_WIDTH) {
                Row(modifier = Modifier.fillMaxSize()) {
                    ChartPane(
                        series = state.series,
                        modifier = Modifier.weight(CHART_PANE_WEIGHT).fillMaxHeight(),
                    )
                    GeneratorList(
                        generators = state.generators,
                        onVisibilityChanged = onVisibilityChanged,
                        modifier = Modifier.weight(LIST_PANE_WEIGHT).fillMaxHeight(),
                    )
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    ChartPane(
                        series = state.series,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                    GeneratorList(
                        generators = state.generators,
                        onVisibilityChanged = onVisibilityChanged,
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartPane(series: PersistentList<SignalSeriesUiModel>, modifier: Modifier = Modifier) {
    val visibleSeries = remember(series) { series.filter(SignalSeriesUiModel::isVisible) }
    Column(modifier = modifier.padding(SignalMonitorSpacing.medium)) {
        SignalLegend(
            series = visibleSeries,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(SignalMonitorSpacing.small))
        SignalChart(
            series = series,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SignalLegend(series: List<SignalSeriesUiModel>, modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.testTag(SIGNAL_LEGEND),
        horizontalArrangement = Arrangement.spacedBy(SignalMonitorSpacing.medium),
    ) {
        items(
            items = series,
            key = { item -> item.id.value },
        ) { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(LEGEND_INDICATOR_SIZE)
                        .background(Color(item.colorArgb), CircleShape),
                )
                Spacer(modifier = Modifier.width(SignalMonitorSpacing.small))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun GeneratorList(
    generators: PersistentList<GeneratorItemUiModel>,
    onVisibilityChanged: (GeneratorId, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag(GENERATOR_LIST),
        ) {
            items(
                items = generators,
                key = { item -> item.id.value },
            ) { item ->
                GeneratorItem(
                    item = item,
                    onVisibilityChanged = { isVisible -> onVisibilityChanged(item.id, isVisible) },
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = SignalMonitorSpacing.large),
                )
            }
        }
    }
}

@Composable
private fun GeneratorItem(
    item: GeneratorItemUiModel,
    onVisibilityChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag(generatorItem(item.id))
            .padding(
                horizontal = SignalMonitorSpacing.medium,
                vertical = SignalMonitorSpacing.small,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = item.isVisible,
            onCheckedChange = onVisibilityChanged,
            modifier = Modifier
                .testTag(generatorCheckbox(item.id))
                .semantics { contentDescription = item.name },
        )
        Box(
            modifier = Modifier
                .size(GENERATOR_INDICATOR_SIZE)
                .background(Color(item.colorArgb), CircleShape),
        )
        Spacer(modifier = Modifier.width(GENERATOR_ITEM_HORIZONTAL_SPACING))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                text = if (item.isCompleted) {
                    stringResource(
                        R.string.generator_completed,
                        formatRemainingTime(item.remainingTimeMillis),
                    )
                } else {
                    formatRemainingTime(item.remainingTimeMillis)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag(generatorTimer(item.id)),
            )
        }
        Text(
            text = formatSignalValue(item.currentValue),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

private fun formatRemainingTime(remainingTimeMillis: Long): String {
    val totalSeconds = (
        remainingTimeMillis + MILLISECONDS_PER_SECOND - 1L
        ) / MILLISECONDS_PER_SECOND
    val minutes = totalSeconds / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    return "%02d:%02d".format(Locale.US, minutes, seconds)
}

private const val CHART_PANE_WEIGHT = 3f
private const val LIST_PANE_WEIGHT = 2f
private const val MILLISECONDS_PER_SECOND = 1_000L
private const val SECONDS_PER_MINUTE = 60L
private val EXPANDED_LAYOUT_MIN_WIDTH = 720.dp
private val LEGEND_INDICATOR_SIZE = 10.dp
private val GENERATOR_INDICATOR_SIZE = 12.dp
private val GENERATOR_ITEM_HORIZONTAL_SPACING = 10.dp

@Preview(showBackground = true, heightDp = 800)
@Preview(
    showBackground = true,
    heightDp = 800,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SignalMonitorScreenPreview() {
    SignalMonitorTheme {
        val previewSignalColorArgb = MaterialTheme.colorScheme.primary.toArgb()
        val generators = List(10) { index ->
            GeneratorItemUiModel(
                id = GeneratorId(index + 1),
                name = "Generator #${index + 1}",
                colorArgb = previewSignalColorArgb,
                remainingTimeMillis = (index + 1) * 75_000L,
                currentValue = index * 1.25,
                isVisible = true,
                isCompleted = false,
            )
        }.toPersistentList()
        SignalMonitorScreen(
            state = SignalMonitorUiState(generators = generators),
            onIntent = {},
        )
    }
}

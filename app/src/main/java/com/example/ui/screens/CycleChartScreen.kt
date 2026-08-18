package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.data.model.HypnogramPoint
import com.example.data.model.SleepStage
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassSegmentedTabs
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialPink
import com.example.ui.theme.StageAwakeColor
import com.example.ui.theme.StageDeepColor
import com.example.ui.theme.StageLightColor
import com.example.ui.theme.StageRemColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SleepViewModel

@Composable
fun CycleChartScreen(
    viewModel: SleepViewModel,
    lang: AppLanguage = viewModel.appLanguage.collectAsState().value,
    usePersianDigits: Boolean = viewModel.usePersianDigits.collectAsState().value,
    modifier: Modifier = Modifier
) {
    val points by viewModel.hypnogramPoints.collectAsState()
    val selectedCycles by viewModel.selectedChartCycle.collectAsState()
    val scrubbedMin by viewModel.scrubbedMinute.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = LocalizedStrings.get("chart_title", lang),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = LocalizedStrings.get("chart_subtitle", lang),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Cycle Selector
        item {
            GlassSegmentedTabs(
                items = listOf(3, 4, 5, 6),
                selectedItem = selectedCycles,
                onItemSelected = { viewModel.selectChartCycle(it) },
                itemLabel = { c ->
                    val num = LocalizedStrings.formatDigits(c.toString(), lang, usePersianDigits)
                    val label = if (lang == AppLanguage.PERSIAN) "چرخه" else "Cycles"
                    "$num $label"
                }
            )
        }

        // Hypnogram Canvas Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hypnogram_chart_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val totalDurationMin = selectedCycles * 90 + 15
                        val formattedDuration = LocalizedStrings.formatMinutes(totalDurationMin, lang, usePersianDigits)

                        Text(
                            text = if (lang == AppLanguage.PERSIAN) "ساختار معماری خواب ($formattedDuration)" else "Architecture Flow ($formattedDuration)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialCyan
                        )

                        Text(
                            text = LocalizedStrings.get("chart_scrub_prompt", lang),
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Hypnogram Visual Canvas
                    HypnogramCanvas(
                        points = points,
                        totalCycles = selectedCycles,
                        scrubbedMinute = scrubbedMin,
                        onScrub = { viewModel.setScrubbedMinute(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend of stages
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StageLegendItem(color = StageAwakeColor, label = if (lang == AppLanguage.PERSIAN) "بیداری" else "Awake")
                        StageLegendItem(color = StageRemColor, label = if (lang == AppLanguage.PERSIAN) "رویایی (REM)" else "REM")
                        StageLegendItem(color = StageLightColor, label = if (lang == AppLanguage.PERSIAN) "سبک (N2)" else "Light")
                        StageLegendItem(color = StageDeepColor, label = if (lang == AppLanguage.PERSIAN) "عمیق (N3)" else "Deep")
                    }
                }
            }
        }

        // Deep Sleep Science Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(StageDeepColor)
                        )
                        Text(
                            text = LocalizedStrings.get("chart_deep_science_title", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = LocalizedStrings.get("chart_deep_science_body", lang),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // REM Sleep Science Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(StageRemColor)
                        )
                        Text(
                            text = LocalizedStrings.get("chart_rem_science_title", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = LocalizedStrings.get("chart_rem_science_body", lang),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 90-Minute Cycle Science Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null, tint = CelestialCyan, modifier = Modifier.size(18.dp))
                        Text(
                            text = LocalizedStrings.get("chart_science_title", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = LocalizedStrings.get("chart_science_body", lang),
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StageLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun HypnogramCanvas(
    points: List<HypnogramPoint>,
    totalCycles: Int,
    scrubbedMinute: Int?,
    onScrub: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val totalDuration = points.sumOf { it.durationMinutes }.coerceAtLeast(1)

    Canvas(
        modifier = modifier
            .pointerInput(totalDuration) {
                detectTapGestures(
                    onPress = { offset ->
                        val scrubFraction = (offset.x / size.width).coerceIn(0f, 1f)
                        val min = (scrubFraction * totalDuration).toInt()
                        onScrub(min)
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height

        val yLevels = mapOf(
            SleepStage.AWAKE to 0.12f * h,
            SleepStage.REM to 0.38f * h,
            SleepStage.LIGHT to 0.65f * h,
            SleepStage.DEEP to 0.90f * h
        )

        // Draw horizontal grid guide lines
        yLevels.forEach { (_, yPos) ->
            drawLine(
                color = Color(0x18FFFFFF),
                start = Offset(0f, yPos),
                end = Offset(w, yPos),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw Cycle separators
        for (c in 1..totalCycles) {
            val cycleMin = 15 + (c * 90)
            val xPos = (cycleMin.toFloat() / totalDuration.toFloat()) * w
            if (xPos in 0f..w) {
                drawLine(
                    color = Color(0x2506B6D4),
                    start = Offset(xPos, 0f),
                    end = Offset(xPos, h),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        // Build continuous staircase hypnogram path
        val path = Path()
        var currentX = 0f
        val firstPoint = points.firstOrNull() ?: return@Canvas
        var prevY = yLevels[firstPoint.stage] ?: (0.12f * h)

        path.moveTo(0f, prevY)

        points.forEach { point ->
            val segWidth = (point.durationMinutes.toFloat() / totalDuration.toFloat()) * w
            val targetY = yLevels[point.stage] ?: (0.65f * h)
            val nextX = currentX + segWidth

            // Vertical step to target stage
            path.lineTo(currentX, targetY)
            // Horizontal hold for duration
            path.lineTo(nextX, targetY)

            // Fill color block for visual impact
            drawRect(
                color = point.stage.color.copy(alpha = 0.30f),
                topLeft = Offset(currentX, targetY),
                size = Size(segWidth, h - targetY)
            )

            currentX = nextX
            prevY = targetY
        }

        // Stroke the outline curve
        drawPath(
            path = path,
            color = CelestialCyan,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // Draw Scrub indicator if present
        scrubbedMinute?.let { min ->
            val scrubX = (min.toFloat() / totalDuration.toFloat()) * w
            drawLine(
                color = CelestialPink,
                start = Offset(scrubX, 0f),
                end = Offset(scrubX, h),
                strokeWidth = 2.dp.toPx()
            )
            drawCircle(
                color = CelestialPink,
                radius = 5.dp.toPx(),
                center = Offset(scrubX, 10f)
            )
        }
    }
}

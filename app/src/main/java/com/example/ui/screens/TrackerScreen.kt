package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.TableChart
import com.example.data.export.SleepDataExporter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SleepLogEntity
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTimeSelector
import com.example.ui.components.SleepQualityGauge
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CelestialPink
import com.example.ui.theme.CelestialTeal
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.QualityOptimal
import com.example.ui.theme.StageDeepColor
import com.example.ui.theme.StageLightColor
import com.example.ui.theme.StageRemColor
import com.example.ui.theme.TextAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SleepViewModel
import java.util.Locale

@Composable
fun TrackerScreen(
    viewModel: SleepViewModel,
    lang: AppLanguage = viewModel.appLanguage.collectAsState().value,
    usePersianDigits: Boolean = viewModel.usePersianDigits.collectAsState().value,
    modifier: Modifier = Modifier
) {
    val isFa = lang == AppLanguage.PERSIAN

    val bedH by viewModel.trackerBedHour.collectAsState()
    val bedM by viewModel.trackerBedMinute.collectAsState()
    val wakeH by viewModel.trackerWakeHour.collectAsState()
    val wakeM by viewModel.trackerWakeMinute.collectAsState()
    val result by viewModel.trackerResult.collectAsState()

    val isBreathing by viewModel.isBreathingActive.collectAsState()
    val breathPhase by viewModel.breathingPhase.collectAsState()
    val breathSec by viewModel.breathingSecondsLeft.collectAsState()

    val allLogs by viewModel.allLogs.collectAsState()
    val goal by viewModel.sleepGoal.collectAsState()
    val context = LocalContext.current

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
                    text = LocalizedStrings.get("tracker_title", lang),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = LocalizedStrings.get("tracker_subtitle", lang),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Bedtime Input Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.NightlightRound, contentDescription = null, tint = CelestialViolet, modifier = Modifier.size(20.dp))
                        Text(
                            text = LocalizedStrings.get("tracker_bedtime_label", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GlassTimeSelector(
                        hour = bedH,
                        minute = bedM,
                        onTimeChanged = { h, m -> viewModel.setTrackerTimes(h, m, wakeH, wakeM) },
                        lang = lang,
                        usePersianDigits = usePersianDigits
                    )
                }
            }
        }

        // Wake Time Input Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.WbSunny, contentDescription = null, tint = CelestialCyan, modifier = Modifier.size(20.dp))
                        Text(
                            text = LocalizedStrings.get("tracker_waketime_label", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GlassTimeSelector(
                        hour = wakeH,
                        minute = wakeM,
                        onTimeChanged = { h, m -> viewModel.setTrackerTimes(bedH, bedM, h, m) },
                        lang = lang,
                        usePersianDigits = usePersianDigits
                    )
                }
            }
        }

        // Calculated Result Dashboard
        result?.let { res ->
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tracker_results_card"),
                    borderBrush = Brush.linearGradient(listOf(CelestialCyan, CelestialIndigo))
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val cyclesDisplay = LocalizedStrings.formatDigits(String.format(Locale.US, "%.1f", res.cyclesCount), lang, usePersianDigits)
                                val durationDisplay = LocalizedStrings.formatMinutes(res.totalMinutes, lang, usePersianDigits)

                                Text(
                                    text = LocalizedStrings.get("tracker_cycles_passed", lang),
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = if (isFa) "$cyclesDisplay چرخه خواب" else "$cyclesDisplay Cycles Completed",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isFa) "مجموع استراحت: $durationDisplay" else "Total Duration: $durationDisplay",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextAccent
                                )
                            }

                            // Quality Gauge
                            SleepQualityGauge(
                                score = res.qualityScore,
                                label = if (isFa) "کیفیت" else "Quality"
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stage Breakdown Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StageStatBox(
                                title = LocalizedStrings.get("tracker_deep_achieved", lang),
                                value = LocalizedStrings.formatMinutes(res.deepSleepMinutes, lang, usePersianDigits),
                                color = StageDeepColor,
                                modifier = Modifier.weight(1f)
                            )
                            StageStatBox(
                                title = LocalizedStrings.get("tracker_rem_achieved", lang),
                                value = LocalizedStrings.formatMinutes(res.remMinutes, lang, usePersianDigits),
                                color = StageRemColor,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StageStatBox(
                                title = LocalizedStrings.get("tracker_light_achieved", lang),
                                value = LocalizedStrings.formatMinutes(res.lightMinutes, lang, usePersianDigits),
                                color = StageLightColor,
                                modifier = Modifier.weight(1f)
                            )
                            val effDisplay = LocalizedStrings.formatDigits("${res.sleepEfficiency}%", lang, usePersianDigits)
                            StageStatBox(
                                title = LocalizedStrings.get("tracker_efficiency", lang),
                                value = effDisplay,
                                color = CelestialTeal,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Save Log Button
                        GlassButton(
                            text = LocalizedStrings.get("tracker_save_btn", lang),
                            onClick = { viewModel.saveCurrentTrackerSleep() },
                            icon = {
                                Icon(Icons.Default.BookmarkAdded, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "save_sleep_log_btn"
                        )
                    }
                }
            }
        }

        // 4-7-8 Relaxing Breathing Guide
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("breathing_guide_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Air, contentDescription = null, tint = CelestialTeal, modifier = Modifier.size(20.dp))
                        Text(
                            text = LocalizedStrings.get("tracker_breathing_title", lang),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = LocalizedStrings.get("tracker_breathing_sub", lang),
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Animated Breathing Circle
                    val targetScale = when (breathPhase) {
                        "tracker_breathe_in" -> 1.35f
                        "tracker_breathe_hold" -> 1.35f
                        else -> 0.85f
                    }
                    val animatedScale by animateFloatAsState(
                        targetValue = if (isBreathing) targetScale else 1.0f,
                        animationSpec = tween(
                            durationMillis = if (breathPhase == "tracker_breathe_in") 4000 else if (breathPhase == "tracker_breathe_hold") 1000 else 8000,
                            easing = FastOutSlowInEasing
                        ),
                        label = "breathing_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(animatedScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(CelestialTeal.copy(alpha = 0.5f), CelestialIndigo.copy(alpha = 0.2f), Color.Transparent)
                                )
                            )
                            .border(2.dp, CelestialTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isBreathing) {
                                val secDisplay = LocalizedStrings.formatDigits(breathSec.toString(), lang, usePersianDigits)
                                Text(
                                    text = secDisplay,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Icon(Icons.Default.SelfImprovement, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    AnimatedVisibility(visible = isBreathing) {
                        Text(
                            text = LocalizedStrings.get(breathPhase, lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GlassButton(
                        text = if (isBreathing) LocalizedStrings.get("tracker_stop_breathing", lang) else LocalizedStrings.get("tracker_start_breathing", lang),
                        onClick = { viewModel.toggleBreather() },
                        gradient = if (isBreathing) Brush.horizontalGradient(listOf(CelestialPink, CelestialViolet)) else Brush.horizontalGradient(listOf(CelestialTeal, CelestialCyan)),
                        modifier = Modifier.width(200.dp),
                        testTag = "toggle_breathing_btn"
                    )
                }
            }
        }

        // Recent Saved Sleep Logs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalizedStrings.get("tracker_history_title", lang),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                if (allLogs.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x25FFFFFF))
                                .clickable {
                                    val json = SleepDataExporter.exportToJson(allLogs, goal)
                                    SleepDataExporter.shareContent(context, json, "Somno Sleep Data JSON", "application/json")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CelestialCyan)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x25FFFFFF))
                                .clickable {
                                    val csv = SleepDataExporter.exportToCsv(allLogs)
                                    SleepDataExporter.shareContent(context, csv, "Somno Sleep Data CSV", "text/csv")
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CelestialTeal)
                        }
                    }
                }
            }
        }

        if (allLogs.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = LocalizedStrings.get("tracker_no_logs", lang),
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(allLogs) { log ->
                SavedSleepLogItem(
                    log = log,
                    lang = lang,
                    usePersianDigits = usePersianDigits,
                    onDelete = { viewModel.deleteLog(log.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun StageStatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MidnightCard.copy(alpha = 0.7f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 10.sp,
                color = color,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun SavedSleepLogItem(
    log: SleepLogEntity,
    lang: AppLanguage,
    usePersianDigits: Boolean,
    onDelete: () -> Unit
) {
    val isFa = lang == AppLanguage.PERSIAN
    val bedFormatted = LocalizedStrings.formatDigits(String.format(Locale.US, "%02d:%02d", log.bedHour, log.bedMinute), lang, usePersianDigits)
    val wakeFormatted = LocalizedStrings.formatDigits(String.format(Locale.US, "%02d:%02d", log.wakeHour, log.wakeMinute), lang, usePersianDigits)
    val durationFormatted = LocalizedStrings.formatMinutes(log.totalDurationMinutes, lang, usePersianDigits)
    val cyclesFormatted = LocalizedStrings.formatDigits(String.format(Locale.US, "%.1f", log.cyclesCount), lang, usePersianDigits)
    val deepFormatted = LocalizedStrings.formatMinutes(log.deepSleepMinutes, lang, usePersianDigits)

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("log_item_${log.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$bedFormatted → $wakeFormatted ($durationFormatted)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isFa) "$cyclesFormatted چرخه • خواب عمیق: $deepFormatted" else "$cyclesFormatted cycles • Deep: $deepFormatted",
                    fontSize = 12.sp,
                    color = TextAccent
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Score pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(QualityOptimal.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val scoreDisplay = LocalizedStrings.formatDigits("${log.qualityScore}", lang, usePersianDigits)
                    Text(
                        text = "$scoreDisplay/100",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = QualityOptimal
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

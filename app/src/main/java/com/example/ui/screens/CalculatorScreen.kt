package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.data.model.CalculationMode
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassSegmentedTabs
import com.example.ui.components.GlassTimeSelector
import com.example.ui.components.SleepRecommendationCard
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SleepViewModel

@Composable
fun CalculatorScreen(
    viewModel: SleepViewModel,
    lang: AppLanguage,
    usePersianDigits: Boolean,
    modifier: Modifier = Modifier
) {
    val mode by viewModel.calculationMode.collectAsState()
    val targetHour by viewModel.targetHour.collectAsState()
    val targetMinute by viewModel.targetMinute.collectAsState()
    val recs by viewModel.recommendations.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Mode Selector Tabs
        item {
            Spacer(modifier = Modifier.height(4.dp))
            GlassSegmentedTabs(
                items = listOf(CalculationMode.WAKE_UP, CalculationMode.BEDTIME, CalculationMode.SLEEP_NOW),
                selectedItem = mode,
                onItemSelected = { viewModel.setCalculationMode(it) },
                itemLabel = { m ->
                    when (m) {
                        CalculationMode.WAKE_UP -> LocalizedStrings.get("calc_mode_wake_up", lang)
                        CalculationMode.BEDTIME -> LocalizedStrings.get("calc_mode_bedtime", lang)
                        CalculationMode.SLEEP_NOW -> LocalizedStrings.get("calc_mode_sleep_now", lang)
                    }
                }
            )
        }

        // Time Input or Sleep Now Hero Card
        item {
            when (mode) {
                CalculationMode.WAKE_UP -> {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.WbSunny, contentDescription = null, tint = CelestialCyan, modifier = Modifier.size(20.dp))
                                Text(
                                    text = LocalizedStrings.get("calc_header_wake_up", lang),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            GlassTimeSelector(
                                hour = targetHour,
                                minute = targetMinute,
                                onTimeChanged = { h, m -> viewModel.setTargetTime(h, m) },
                                lang = lang,
                                usePersianDigits = usePersianDigits
                            )
                        }
                    }
                }
                CalculationMode.BEDTIME -> {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.NightlightRound, contentDescription = null, tint = CelestialViolet, modifier = Modifier.size(20.dp))
                                Text(
                                    text = LocalizedStrings.get("calc_header_bedtime", lang),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            GlassTimeSelector(
                                hour = targetHour,
                                minute = targetMinute,
                                onTimeChanged = { h, m -> viewModel.setTargetTime(h, m) },
                                lang = lang,
                                usePersianDigits = usePersianDigits
                            )
                        }
                    }
                }
                CalculationMode.SLEEP_NOW -> {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = LocalizedStrings.get("calc_header_sleep_now", lang),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = LocalizedStrings.get("calc_sleep_latency_note", lang),
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            GlassButton(
                                text = LocalizedStrings.get("calc_sleep_now_btn", lang),
                                onClick = { viewModel.setSleepNow() },
                                icon = {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                testTag = "sleep_now_button"
                            )
                        }
                    }
                }
            }
        }

        // Section Title for Results
        item {
            val title = when (mode) {
                CalculationMode.WAKE_UP -> if (lang == AppLanguage.PERSIAN) "ساعت‌های پیشنهادی برای خوابیدن:" else "Optimal Times to Fall Asleep:"
                CalculationMode.BEDTIME, CalculationMode.SLEEP_NOW -> if (lang == AppLanguage.PERSIAN) "ساعت‌های پیشنهادی برای بیدار شدن:" else "Optimal Times to Wake Up:"
            }
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // List of Recommendations
        items(recs) { rec ->
            SleepRecommendationCard(
                recommendation = rec,
                lang = lang,
                usePersianDigits = usePersianDigits,
                onSetAlarm = { h, m ->
                    viewModel.setAlarmForTime(h, m)
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

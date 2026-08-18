package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataArray
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.export.SleepDataExporter
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CelestialTeal
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.QualityPoor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SleepViewModel

@Composable
fun SettingsScreen(
    viewModel: SleepViewModel,
    lang: AppLanguage = viewModel.appLanguage.collectAsState().value,
    usePersianDigits: Boolean = viewModel.usePersianDigits.collectAsState().value,
    modifier: Modifier = Modifier
) {
    val latency by viewModel.sleepLatencyMinutes.collectAsState()
    val allLogs by viewModel.allLogs.collectAsState()
    val goal by viewModel.sleepGoal.collectAsState()
    val isFa = lang == AppLanguage.PERSIAN
    val context = LocalContext.current

    var showClearDialog by remember { mutableStateOf(false) }

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
                    text = LocalizedStrings.get("settings_title", lang),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = if (isFa) "شخصی‌سازی زبان، خروجی JSON داده‌ها و تنظیمات خواب" else "Language, JSON data export, and sleep preferences",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Language & Localization Settings
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = CelestialCyan, modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = LocalizedStrings.get("settings_lang", lang),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = LocalizedStrings.get("settings_lang_sub", lang),
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        // Language Toggle Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            LanguageChip(
                                label = "English",
                                isSelected = lang == AppLanguage.ENGLISH,
                                onClick = { viewModel.setLanguage(AppLanguage.ENGLISH) }
                            )
                            LanguageChip(
                                label = "فارسی",
                                isSelected = lang == AppLanguage.PERSIAN,
                                onClick = { viewModel.setLanguage(AppLanguage.PERSIAN) }
                            )
                        }
                    }

                    if (lang == AppLanguage.PERSIAN) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = LocalizedStrings.get("settings_persian_digits", lang),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = LocalizedStrings.get("settings_persian_digits_sub", lang),
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            Switch(
                                checked = usePersianDigits,
                                onCheckedChange = { viewModel.setUsePersianDigits(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = CelestialCyan,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = Color(0x30131C31)
                                )
                            )
                        }
                    }
                }
            }
        }

        // Sleep Latency Setting
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = CelestialViolet, modifier = Modifier.size(20.dp))
                            Column {
                                Text(
                                    text = LocalizedStrings.get("settings_latency", lang),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = LocalizedStrings.get("settings_latency_sub", lang),
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        val latDisplay = LocalizedStrings.formatDigits("$latency min", lang, usePersianDigits)
                        Text(
                            text = latDisplay,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CelestialCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = latency.toFloat(),
                        onValueChange = { viewModel.setSleepLatency(it.toInt()) },
                        valueRange = 5f..35f,
                        steps = 5,
                        colors = SliderDefaults.colors(
                            thumbColor = CelestialCyan,
                            activeTrackColor = CelestialCyan,
                            inactiveTrackColor = Color(0x30131C31)
                        )
                    )
                }
            }
        }

        // Data Export Card (JSON, CSV, Summary Text)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("export_data_card"),
                borderBrush = Brush.linearGradient(listOf(CelestialTeal.copy(alpha = 0.6f), CelestialCyan.copy(alpha = 0.6f)))
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = CelestialTeal, modifier = Modifier.size(22.dp))
                        Column {
                            Text(
                                text = LocalizedStrings.get("settings_export_title", lang),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = LocalizedStrings.get("settings_export_sub", lang),
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row of Export Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Export JSON
                        GlassButton(
                            text = "JSON",
                            icon = { Icon(Icons.Default.DataArray, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                val jsonStr = SleepDataExporter.exportToJson(allLogs, goal)
                                SleepDataExporter.shareContent(context, jsonStr, "Somno Sleep Data JSON", "application/json")
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "export_json_btn"
                        )

                        // Export CSV
                        GlassButton(
                            text = "CSV",
                            icon = { Icon(Icons.Default.TableChart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                val csvStr = SleepDataExporter.exportToCsv(allLogs)
                                SleepDataExporter.shareContent(context, csvStr, "Somno Sleep Data CSV", "text/csv")
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "export_csv_btn"
                        )

                        // Copy Summary Report
                        GlassButton(
                            text = if (isFa) "متن" else "Report",
                            icon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp)) },
                            onClick = {
                                val summary = SleepDataExporter.exportToSummaryText(allLogs, goal, lang)
                                SleepDataExporter.copyToClipboard(
                                    context = context,
                                    text = summary,
                                    label = "Somno Sleep Report",
                                    feedbackMessage = LocalizedStrings.get("settings_copied", lang)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "export_summary_btn"
                        )
                    }
                }
            }
        }

        // Clear History
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showClearDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = QualityPoor, modifier = Modifier.size(20.dp))
                        Text(
                            text = LocalizedStrings.get("settings_clear_data", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = QualityPoor
                        )
                    }
                }
            }
        }

        // Creator & Telegram Credit Card (Mandatory Requirement)
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("creator_credit_card"),
                borderBrush = Brush.linearGradient(listOf(CelestialCyan, CelestialIndigo)),
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Shibadev_Copy"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(CelestialCyan, CelestialIndigo))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        Column {
                            Text(
                                text = LocalizedStrings.get("settings_credit", lang),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Telegram: @Shibadev_Copy",
                                fontSize = 12.sp,
                                color = CelestialCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = LocalizedStrings.get("settings_version", lang),
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(text = LocalizedStrings.get("settings_clear_data", lang), color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(text = LocalizedStrings.get("settings_clear_confirm", lang), color = TextSecondary)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    }
                ) {
                    Text(text = LocalizedStrings.get("settings_confirm", lang), color = QualityPoor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = LocalizedStrings.get("settings_cancel", lang), color = TextSecondary)
                }
            },
            containerColor = Color(0xFF131C31),
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun LanguageChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) CelestialCyan.copy(alpha = 0.3f) else Color(0x25FFFFFF))
            .border(1.dp, if (isSelected) CelestialCyan else GlassBorder, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) TextPrimary else TextMuted
        )
    }
}

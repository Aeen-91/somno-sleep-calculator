package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.data.model.Chronotype
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.CelestialAmber
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CelestialTeal
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MidnightCard
import com.example.ui.theme.QualityOptimal
import com.example.ui.theme.TextAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SleepViewModel

@Composable
fun AICoachScreen(
    viewModel: SleepViewModel,
    lang: AppLanguage = viewModel.appLanguage.collectAsState().value,
    usePersianDigits: Boolean = viewModel.usePersianDigits.collectAsState().value,
    modifier: Modifier = Modifier
) {
    val isFa = lang == AppLanguage.PERSIAN

    val goal by viewModel.sleepGoal.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingAi.collectAsState()
    val report by viewModel.aiAnalysisReport.collectAsState()

    val q1 by viewModel.quizSelectedOption1.collectAsState()
    val q2 by viewModel.quizSelectedOption2.collectAsState()
    val q3 by viewModel.quizSelectedOption3.collectAsState()

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
                    text = LocalizedStrings.get("ai_title", lang),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = LocalizedStrings.get("ai_subtitle", lang),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Chronotype Selection Card
        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("chronotype_card")
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = CelestialCyan, modifier = Modifier.size(20.dp))
                        Text(
                            text = LocalizedStrings.get("ai_chronotype_title", lang),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chronotype 2x2 Grid
                    val types = Chronotype.values()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ChronotypePill(
                                type = types[0],
                                isSelected = goal.chronotype == types[0],
                                lang = lang,
                                onSelect = { viewModel.setChronotype(types[0]) },
                                modifier = Modifier.weight(1f)
                            )
                            ChronotypePill(
                                type = types[1],
                                isSelected = goal.chronotype == types[1],
                                lang = lang,
                                onSelect = { viewModel.setChronotype(types[1]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ChronotypePill(
                                type = types[2],
                                isSelected = goal.chronotype == types[2],
                                lang = lang,
                                onSelect = { viewModel.setChronotype(types[2]) },
                                modifier = Modifier.weight(1f)
                            )
                            ChronotypePill(
                                type = types[3],
                                isSelected = goal.chronotype == types[3],
                                lang = lang,
                                onSelect = { viewModel.setChronotype(types[3]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = LocalizedStrings.get(goal.chronotype.descKey, lang),
                        fontSize = 11.sp,
                        color = TextAccent
                    )
                }
            }
        }

        // 3-Question Chronotype Finder Quiz
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Quiz, contentDescription = null, tint = CelestialViolet, modifier = Modifier.size(18.dp))
                        Text(
                            text = LocalizedStrings.get("ai_quiz_title", lang),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Q1: Morning Wakeup feeling
                    QuizQuestion(
                        title = if (isFa) "۱. پس از بیدار شدن در صبح معمولاً چگونه‌اید؟" else "1. How do you feel when waking up in the morning?",
                        options = if (isFa) listOf("پرانرژی و بدون نیاز به آلارم", "کمی گیج تا وقتی چای یا قهوه بنوشم", "خسته و بی‌میل به بیدار شدن قبل از ظهر", "سبک و حساس با کوچکترین صدایی بیدار می‌شوم")
                        else listOf("Alert & ready without alarm", "Groggy until morning caffeine", "Exhausted, prefer sleeping past noon", "Light & easily awakened by noise"),
                        selectedIndex = q1,
                        onOptionSelected = { viewModel.submitQuizAnswer(1, it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Q2: Peak Focus Hours
                    QuizQuestion(
                        title = if (isFa) "۲. اوج تمرکز فکری شما در چه ساعاتی است؟" else "2. What are your peak focus & productivity hours?",
                        options = if (isFa) listOf("ساعات اولیه صبح (۶ تا ۹ صبح)", "اواسط روز (۱۰ صبح تا ۲ بعدازظهر)", "عصر و ساعات پایانی شب (۶ عصر به بعد)", "در طول روز متغیر و نامنظم")
                        else listOf("Early morning (6 AM - 9 AM)", "Midday (10 AM - 2 PM)", "Late evening & night (6 PM+)", "Unpredictable bursts"),
                        selectedIndex = q2,
                        onOptionSelected = { viewModel.submitQuizAnswer(2, it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Q3: Bedtime
                    QuizQuestion(
                        title = if (isFa) "۳. به طور طبیعی چه زمانی احساس خواب‌آلودگی می‌کنید؟" else "3. When do you naturally get sleepy without forcing it?",
                        options = if (isFa) listOf("حدود ۹ تا ۱۰ شب", "حدود ۱۰:۳۰ تا ۱۱:۳۰ شب", "بعد از ۱۲ شب و بامداد", "دیر می‌خوابم چون به سختی به خواب می‌روم")
                        else listOf("9:00 PM - 10:00 PM", "10:30 PM - 11:30 PM", "After 12:00 AM midnight", "Toss & turn before sleep"),
                        selectedIndex = q3,
                        onOptionSelected = { viewModel.submitQuizAnswer(3, it) }
                    )
                }
            }
        }

        // Analyze AI Button
        item {
            GlassButton(
                text = if (report == null) LocalizedStrings.get("ai_btn_analyze", lang) else LocalizedStrings.get("ai_btn_reanalyze", lang),
                onClick = { viewModel.requestAiAnalysis() },
                icon = {
                    if (isAnalyzing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "generate_ai_analysis_btn"
            )
        }

        // AI Generated Analysis Cards
        if (report != null) {
            val rep = report!!
            // Circadian Windows Card
            item {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("circadian_window_card"),
                    borderBrush = Brush.linearGradient(listOf(CelestialCyan, CelestialIndigo))
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.NightsStay, contentDescription = null, tint = CelestialCyan, modifier = Modifier.size(18.dp))
                            Text(
                                text = LocalizedStrings.get("ai_melatonin_window", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rep.circadianWindow,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CelestialCyan
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Coffee, contentDescription = null, tint = CelestialAmber, modifier = Modifier.size(18.dp))
                            Text(
                                text = LocalizedStrings.get("ai_caffeine_cutoff", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rep.caffeineCutoff,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelestialAmber
                        )
                    }
                }
            }

            // Sleep Debt Evaluation Card
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = QualityOptimal, modifier = Modifier.size(18.dp))
                            Text(
                                text = LocalizedStrings.get("ai_sleep_debt_advice", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rep.sleepDebtAnalysis,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Circadian Optimization Protocols (Recommendations)
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.TipsAndUpdates, contentDescription = null, tint = CelestialTeal, modifier = Modifier.size(18.dp))
                            Text(
                                text = LocalizedStrings.get("ai_hygiene_tips", lang),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        rep.recommendations.forEach { tip ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CelestialTeal, modifier = Modifier.size(14.dp))
                                Text(
                                    text = tip,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // Motivational Neuro-Quote
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0x30131C31)
                ) {
                    Text(
                        text = rep.motivationalQuote,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextAccent,
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
private fun ChronotypePill(
    type: Chronotype,
    isSelected: Boolean,
    lang: AppLanguage,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderBrush = if (isSelected) {
        Brush.horizontalGradient(listOf(CelestialCyan, CelestialIndigo))
    } else {
        Brush.verticalGradient(listOf(GlassBorder, GlassBorder))
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) Color(0x40131C31) else MidnightCard.copy(alpha = 0.6f))
            .border(if (isSelected) 1.5.dp else 1.dp, borderBrush, RoundedCornerShape(14.dp))
            .clickable(onClick = onSelect)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = type.iconEmoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = LocalizedStrings.get(type.titleKey, lang),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) CelestialCyan else TextPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun QuizQuestion(
    title: String,
    options: List<String>,
    selectedIndex: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        options.forEachIndexed { index, optionText ->
            val isChosen = selectedIndex == index
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isChosen) Color(0x3506B6D4) else MidnightCard.copy(alpha = 0.5f))
                    .border(1.dp, if (isChosen) CelestialCyan else GlassBorder, RoundedCornerShape(10.dp))
                    .clickable { onOptionSelected(index) }
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = optionText,
                    fontSize = 11.sp,
                    color = if (isChosen) TextPrimary else TextSecondary
                )
            }
        }
    }
}

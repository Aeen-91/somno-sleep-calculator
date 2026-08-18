package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.data.model.SleepQualityRating
import com.example.data.model.SleepRecommendation
import com.example.ui.theme.CelestialCyan
import com.example.ui.theme.CelestialIndigo
import com.example.ui.theme.CelestialPink
import com.example.ui.theme.CelestialViolet
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassHighlight
import com.example.ui.theme.MidnightDark
import com.example.ui.theme.QualityEmergency
import com.example.ui.theme.QualityGood
import com.example.ui.theme.QualityMinimum
import com.example.ui.theme.QualityOptimal
import com.example.ui.theme.StageDeepColor
import com.example.ui.theme.StageRemColor
import com.example.ui.theme.TextAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale
import kotlin.random.Random

/**
 * Ambient cosmic background with gentle pulsing glowing blobs and subtle starry particles.
 */
@Composable
fun GlowingOrbBackground(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val glowOffset1 by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob1"
    )
    val glowOffset2 by infiniteTransition.animateFloat(
        initialValue = 40f,
        targetValue = -70f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blob2"
    )

    // Pre-generate static starry coordinates
    val stars = remember {
        List(30) {
            Triple(Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 1.8f + 0.6f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightDark)
            .drawBehind {
                val width = size.width
                val height = size.height

                // Radial Blob 1 (Celestial Indigo/Violet at top right)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CelestialIndigo.copy(alpha = 0.25f),
                            CelestialViolet.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.85f + glowOffset1, height * 0.18f + glowOffset2),
                        radius = width * 0.75f
                    )
                )

                // Radial Blob 2 (Cyan/Teal at bottom left)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CelestialCyan.copy(alpha = 0.18f),
                            CelestialIndigo.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.15f + glowOffset2, height * 0.75f - glowOffset1),
                        radius = width * 0.85f
                    )
                )

                // Radial Blob 3 (Ethereal Pink center-mid)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CelestialPink.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.5f, height * 0.45f + glowOffset1 * 0.5f),
                        radius = width * 0.6f
                    )
                )

                // Draw tiny stars
                stars.forEach { (xFrac, yFrac, radius) ->
                    drawCircle(
                        color = Color.White.copy(alpha = 0.45f * (radius / 2.4f)),
                        radius = radius,
                        center = Offset(width * xFrac, height * yFrac)
                    )
                }
            }
    )
}

@Composable
fun AmbientLiquidBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        GlowingOrbBackground()
        content()
    }
}

/**
 * Reusable Frosted Liquid Glass Card with subtle gradient borders, specular highlight and optional click.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderBrush: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0x35FFFFFF),
            GlassBorder,
            Color(0x10818CF8)
        )
    ),
    backgroundColor: Color = Color(0x33131C31),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = Color.White)
        ) { onClick() }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderBrush, shape)
            .drawBehind {
                drawLine(
                    color = GlassHighlight,
                    start = Offset(24f, 2f),
                    end = Offset(size.width - 24f, 2f),
                    strokeWidth = 2f
                )
            }
            .then(clickModifier)
            .padding(18.dp)
    ) {
        content()
    }
}

/**
 * Pill-shaped Glass Button with vibrant gradient, ripple and test tag.
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    gradient: Brush = Brush.horizontalGradient(listOf(CelestialIndigo, CelestialViolet)),
    testTag: String = "glass_button",
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(28.dp))
            .background(gradient)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White)
            ) { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Segmented Glass Pill Switcher using index-based options
 */
@Composable
fun GlassSegmentedTabs(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0x30131C31))
            .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            options.forEachIndexed { index, title ->
                val isSelected = (index == selectedIndex)
                val bgBrush = if (isSelected) {
                    Brush.horizontalGradient(listOf(CelestialIndigo.copy(alpha = 0.85f), CelestialViolet.copy(alpha = 0.85f)))
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }
                val textColor = if (isSelected) Color.White else TextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(bgBrush)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) { onOptionSelected(index) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Generic Segmented Glass Pill Switcher
 */
@Composable
fun <T> GlassSegmentedTabs(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0x30131C31))
            .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items.forEach { item ->
                val isSelected = (item == selectedItem)
                val bgBrush = if (isSelected) {
                    Brush.horizontalGradient(listOf(CelestialIndigo.copy(alpha = 0.85f), CelestialViolet.copy(alpha = 0.85f)))
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }
                val textColor = if (isSelected) Color.White else TextSecondary

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(bgBrush)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) { onItemSelected(item) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = itemLabel(item),
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Interactive Liquid Glass Time Selector
 */
@Composable
fun GlassTimeSelector(
    hour: Int,
    minute: Int,
    onTimeChanged: (hour: Int, minute: Int) -> Unit,
    lang: AppLanguage,
    usePersianDigits: Boolean,
    modifier: Modifier = Modifier
) {
    val formattedHour = String.format(Locale.US, "%02d", hour)
    val formattedMinute = String.format(Locale.US, "%02d", minute)

    val hourStr = LocalizedStrings.formatDigits(formattedHour, lang, usePersianDigits)
    val minStr = LocalizedStrings.formatDigits(formattedMinute, lang, usePersianDigits)

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = Color(0x35131C31)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Hour Controller
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable {
                                val nextH = (hour + 1) % 24
                                onTimeChanged(nextH, minute)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Hour +", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = hourStr,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable {
                                val prevH = if (hour - 1 < 0) 23 else hour - 1
                                onTimeChanged(prevH, minute)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Hour -", tint = Color.White)
                    }
                }

                Text(
                    text = ":",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextAccent,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Minute Controller
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable {
                                val nextM = (minute + 5) % 60
                                onTimeChanged(hour, nextM)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Minute +", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = minStr,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable {
                                val prevM = if (minute - 5 < 0) 55 else minute - 5
                                onTimeChanged(hour, prevM)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minute -", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Preset Times
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val presets = listOf(Pair(6, 30), Pair(7, 0), Pair(7, 30), Pair(8, 0))
                presets.forEach { (h, m) ->
                    val isSelected = (h == hour && m == minute)
                    val label = String.format(Locale.US, "%02d:%02d", h, m)
                    val displayLabel = LocalizedStrings.formatDigits(label, lang, usePersianDigits)

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CelestialIndigo else Color(0x25FFFFFF))
                            .clickable { onTimeChanged(h, m) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = displayLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual Sleep Recommendation Card
 */
@Composable
fun SleepRecommendationCard(
    recommendation: SleepRecommendation,
    lang: AppLanguage,
    usePersianDigits: Boolean,
    onSetAlarm: (hour: Int, minute: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val timeFormatted = String.format(Locale.US, "%02d:%02d", recommendation.targetHour, recommendation.targetMinute)
    val displayTime = LocalizedStrings.formatDigits(timeFormatted, lang, usePersianDigits)

    val durationStr = LocalizedStrings.formatMinutes(recommendation.totalSleepMinutes, lang, usePersianDigits)
    val deepStr = LocalizedStrings.formatMinutes(recommendation.estimatedDeepSleepMinutes, lang, usePersianDigits)
    val remStr = LocalizedStrings.formatMinutes(recommendation.estimatedRemMinutes, lang, usePersianDigits)

    val cyclesText = String.format(
        LocalizedStrings.get("calc_cycle_count", lang),
        recommendation.cycles
    ).let { LocalizedStrings.formatDigits(it, lang, usePersianDigits) }

    val badgeLabel = LocalizedStrings.get(recommendation.qualityRating.labelKey, lang)
    val badgeColor = when (recommendation.qualityRating) {
        SleepQualityRating.OPTIMAL -> QualityOptimal
        SleepQualityRating.GOOD -> QualityGood
        SleepQualityRating.MINIMUM -> QualityMinimum
        SleepQualityRating.EMERGENCY -> QualityEmergency
    }

    val cardBorder = if (recommendation.isRecommended) {
        Brush.linearGradient(listOf(CelestialCyan, CelestialIndigo, CelestialViolet))
    } else {
        Brush.linearGradient(listOf(Color(0x30FFFFFF), GlassBorder))
    }

    val cardBg = if (recommendation.isRecommended) {
        Color(0x401E293B)
    } else {
        Color(0x28131C31)
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sleep_rec_card_${recommendation.cycles}"),
        borderBrush = cardBorder,
        backgroundColor = cardBg
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time & Recommended Badge
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = displayTime,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )

                        if (recommendation.isRecommended) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CelestialIndigo)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.PERSIAN) "پیشنهاد طلایی" else "BEST",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$cyclesText ($durationStr)",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Set Alarm Quick Button
                Box(
                    modifier = Modifier
                        .testTag("set_alarm_btn_${recommendation.cycles}")
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (recommendation.isRecommended) Brush.horizontalGradient(listOf(CelestialIndigo, CelestialCyan))
                            else Brush.horizontalGradient(listOf(Color(0x33FFFFFF), Color(0x22818CF8)))
                        )
                        .clickable { onSetAlarm(recommendation.targetHour, recommendation.targetMinute) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Alarm,
                            contentDescription = LocalizedStrings.get("calc_set_alarm", lang),
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LocalizedStrings.get("calc_set_alarm", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stage Estimations Breakdown (Deep & REM badges)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Deep Sleep Estimate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x2238BDF8))
                        .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StageDeepColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.PERSIAN) "عمیق: $deepStr" else "Deep: $deepStr",
                            fontSize = 11.sp,
                            color = StageDeepColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // REM Sleep Estimate
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22EC4899))
                        .border(1.dp, Color(0x33EC4899), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StageRemColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.PERSIAN) "رویا: $remStr" else "REM: $remStr",
                            fontSize = 11.sp,
                            color = StageRemColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating Pill
            Text(
                text = "• $badgeLabel",
                fontSize = 11.sp,
                color = badgeColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Animated Circular Sleep Quality Gauge
 */
@Composable
fun SleepQualityGauge(
    score: Int,
    label: String,
    modifier: Modifier = Modifier,
    gaugeSize: Dp = 110.dp
) {
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(score) {
        animatedProgress.animateTo(
            targetValue = (score / 100f).coerceIn(0f, 1f),
            animationSpec = tween(1000, easing = FastOutSlowInEasing)
        )
    }

    val gradient = Brush.sweepGradient(
        listOf(
            CelestialCyan,
            CelestialIndigo,
            CelestialPink,
            CelestialCyan
        )
    )

    Box(
        modifier = modifier.size(gaugeSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
            val strokeWidth = 10.dp.toPx()

            // Background Track
            drawArc(
                color = Color(0x25FFFFFF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active Progress Arc
            drawArc(
                brush = gradient,
                startAngle = -90f,
                sweepAngle = animatedProgress.value * 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

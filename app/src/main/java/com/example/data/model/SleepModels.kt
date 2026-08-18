package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StageAwakeColor
import com.example.ui.theme.StageDeepColor
import com.example.ui.theme.StageLightColor
import com.example.ui.theme.StageRemColor

enum class CalculationMode {
    WAKE_UP,
    BEDTIME,
    SLEEP_NOW
}

enum class SleepQualityRating(val labelKey: String) {
    OPTIMAL("calc_optimal_badge"),
    GOOD("calc_good_badge"),
    MINIMUM("calc_minimum_badge"),
    EMERGENCY("calc_emergency_badge")
}

enum class SleepStage(val titleKey: String, val color: Color, val level: Int) {
    AWAKE("stage_awake", StageAwakeColor, 0),
    REM("stage_rem", StageRemColor, 1),
    LIGHT("stage_light", StageLightColor, 2),
    DEEP("stage_deep", StageDeepColor, 3)
}

enum class Chronotype(
    val titleKey: String,
    val descKey: String,
    val iconEmoji: String
) {
    BEAR("ai_chronotype_bear", "ai_chronotype_bear_desc", "🐻"),
    LION("ai_chronotype_lion", "ai_chronotype_lion_desc", "🦁"),
    WOLF("ai_chronotype_wolf", "ai_chronotype_wolf_desc", "🐺"),
    DOLPHIN("ai_chronotype_dolphin", "ai_chronotype_dolphin_desc", "🐬")
}

data class SleepRecommendation(
    val cycles: Int,
    val totalSleepMinutes: Int,
    val targetHour: Int,
    val targetMinute: Int,
    val isRecommended: Boolean,
    val qualityRating: SleepQualityRating,
    val estimatedDeepSleepMinutes: Int,
    val estimatedRemMinutes: Int
)

data class HypnogramPoint(
    val minuteOffset: Int,
    val cycleIndex: Int,
    val stage: SleepStage,
    val durationMinutes: Int
)

data class SleepCalculationResult(
    val bedHour: Int,
    val bedMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int,
    val totalMinutes: Int,
    val cyclesCount: Double,
    val deepSleepMinutes: Int,
    val remMinutes: Int,
    val lightMinutes: Int,
    val qualityScore: Int,
    val sleepEfficiency: Int,
    val sleepDebtMinutes: Int
)

data class SleepGoal(
    val targetCycles: Int = 5,
    val targetBedtimeHour: Int = 23,
    val targetBedtimeMinute: Int = 0,
    val targetWakeHour: Int = 7,
    val targetWakeMinute: Int = 0,
    val chronotype: Chronotype = Chronotype.BEAR
)

data class AIAnalysisReport(
    val chronotypeSummary: String,
    val circadianWindow: String,
    val caffeineCutoff: String,
    val sleepDebtAnalysis: String,
    val recommendations: List<String>,
    val motivationalQuote: String
)

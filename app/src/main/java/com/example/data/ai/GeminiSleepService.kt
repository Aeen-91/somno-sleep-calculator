package com.example.data.ai

import com.example.data.db.SleepLogEntity
import com.example.data.localization.AppLanguage
import com.example.data.model.AIAnalysisReport
import com.example.data.model.Chronotype
import com.example.data.model.SleepGoal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * Intelligent Local Sleep Analysis Engine.
 * Generates personalized circadian recommendations, sleep debt analysis,
 * caffeine cutoff metrics, and custom restorative tips based on user sleep patterns.
 */
class GeminiSleepService {

    suspend fun generateSleepAnalysis(
        chronotype: Chronotype,
        goal: SleepGoal,
        recentLogs: List<SleepLogEntity>,
        language: AppLanguage
    ): AIAnalysisReport = withContext(Dispatchers.Default) {
        // Small artificial processing delay to create a polished AI calculation feeling
        delay(400)

        val isFa = language == AppLanguage.PERSIAN
        val count = recentLogs.size
        val avgMinutes = if (count > 0) recentLogs.map { it.totalDurationMinutes }.average().toInt() else 450
        val targetMin = goal.targetCycles * 90
        val diff = avgMinutes - targetMin
        val avgQuality = if (count > 0) recentLogs.map { it.qualityScore }.average().toInt() else 85
        val avgDeep = if (count > 0) recentLogs.map { it.deepSleepMinutes }.average().toInt() else 105

        val chronotypeSummary: String
        val circadianWindow: String
        val caffeineCutoff: String
        val sleepDebtAnalysis: String
        val recommendations = mutableListOf<String>()
        val quote: String

        if (isFa) {
            chronotypeSummary = when (chronotype) {
                Chronotype.BEAR -> "ریتم شبانه‌روزی شما کاملاً با چرخه طلوع و غروب خورشید هماهنگ است. سطح انرژی و هوشیاری شما در ساعات ۱۰ صبح تا ۲ بعدازظهر در بالاترین حد قرار دارد و ترشح ملاتونین از حدود ۱۰:۳۰ شب آغاز می‌شود."
                Chronotype.LION -> "کرونوتایپ شیر (سحرخیز فعال): شما در ساعات آغازین صبح اوج تمرکز و بازدهی ذهنی را دارید. برای حفظ چرخه‌های عمیق N3، خواب زودهنگام در محدوده ۹:۳۰ تا ۱۰:۱۵ شب برایتان ایده‌آل است."
                Chronotype.WOLF -> "کرونوتایپ گرگ (شب‌بیدار خلاق): فاز ریتم سیرکادین شما با تاخیر عمل می‌کند. اوج خلاقیت و هوشیاری شما از عصر تا اواخر شب فعال می‌شود و خواب منظم در نیمه‌شب بیشترین بازدهی را به شما می‌دهد."
                Chronotype.DOLPHIN -> "کرونوتایپ دلفین (خواب سبک و حساس): مغز شما حتی در طول استراحت سطح بالایی از تحریک‌پذیری دارد. شما نیاز به تاریکی مطلق، خنکی محیط و آرامش ذهنی برای تثبیت خواب عمیق دارید."
            }

            circadianWindow = when (chronotype) {
                Chronotype.BEAR -> "۱۰:۴۵ شب تا ۱۱:۱۵ شب (پیک طبیعی ملاتونین)"
                Chronotype.LION -> "۹:۳۰ شب تا ۱۰:۱۵ شب"
                Chronotype.WOLF -> "۱۲:۱۵ شب تا ۱:۰۰ بامداد"
                Chronotype.DOLPHIN -> "۱۱:۳۰ شب تا ۱۲:۰۰ شب"
            }

            caffeineCutoff = when (chronotype) {
                Chronotype.LION -> "۱:۳۰ بعدازظهر (حداقل ۸ ساعت قبل از خواب)"
                Chronotype.BEAR -> "۲:۳۰ بعدازظهر (حداقل ۸ ساعت قبل از خواب)"
                Chronotype.WOLF -> "۴:۳۰ بعدازظهر"
                Chronotype.DOLPHIN -> "۱:۰۰ بعدازظهر"
            }

            sleepDebtAnalysis = when {
                count == 0 -> "هنوز داده‌ای ثبت نشده است؛ بر اساس هدف ${goal.targetCycles} چرخه (${goal.targetCycles * 90 / 60} ساعت)، ریتم شما در حالت پایه قرار دارد."
                diff >= 15 -> "تراز خواب عالی (+${diff} دقیقه مازاد بر هدف). سیستم عصبی و حافظه شما در وضعیت ریکاوری کامل قرار دارند."
                diff >= -30 -> "تعادل مطلوب خواب (${kotlin.math.abs(diff)} دقیقه کسری جزئی). با خواب ۵ چرخه‌ای استاندارد امشب به راحتی جبران می‌شود."
                else -> "بدهی خواب انباشته (${kotlin.math.abs(diff)} دقیقه کمتر از هدف). توصیه می‌شود امشب ۶ چرخه خواب کامل (۹ ساعت) برای بازسازی نورونی در نظر بگیرید."
            }

            // Tailored dynamic tips
            if (avgDeep < 80 && count > 0) {
                recommendations.add("میزان خواب عمیق شما کمتر از حد بهینه است؛ دمای اتاق را روی ۱۸ تا ۱۹ درجه سانتی‌گراد تنظیم کنید.")
            } else {
                recommendations.add("برای تثبیت خواب عمیق N3، ۳۰ دقیقه قبل از خواب نورهای آبی و صفحات نمایش را به طور کامل خاموش کنید.")
            }

            if (chronotype == Chronotype.WOLF) {
                recommendations.add("صبح‌ها بلافاصله پس از بیداری ۱۰ دقیقه در معرض نور خورشید قرار بگیرید تا ترشح کورتیزول تنظیم شود.")
            } else {
                recommendations.add("برنامه بیداری ثابت در تمام روزهای هفته حتی تعطیلات به تثبیت ساعت درونی بدن کمک شایانی می‌کند.")
            }

            recommendations.add("از تکنیک آرام‌بخش تنفس ۴-۷-۸ در بخش ردیاب سمنو قبل از خواب استفاده کنید تا تپش قلب کاهش یابد.")
            recommendations.add("حداقل ۲ تا ۳ ساعت قبل از رفتن به رختخواب از صرف وعده‌های غذایی سنگین خودداری کنید.")

            quote = "«خواب آرام، زنجیره زرینی است که سلامتی و انرژی تن و روان را به یکدیگر پیوند می‌دهد.»"
        } else {
            chronotypeSummary = when (chronotype) {
                Chronotype.BEAR -> "Your circadian rhythm aligns harmoniously with the solar cycle. Peak cognitive clarity occurs between 10:00 AM and 2:00 PM, with natural melatonin elevation starting around 10:30 PM."
                Chronotype.LION -> "Lion Chronotype (Early Morning Achiever): You experience peak cortisol and mental focus early in the morning. An early bedtime between 9:30 PM and 10:15 PM preserves restorative Stage 3 Slow Wave sleep."
                Chronotype.WOLF -> "Wolf Chronotype (Night Owl): Your circadian phase is shifted later. Peak creativity and alertness arrive in the evening hours. A consistent bedtime around 12:15 AM to 1:00 AM matches your natural biology best."
                Chronotype.DOLPHIN -> "Dolphin Chronotype (Sensitive Sleeper): You possess an active nervous system during rest. Low ambient noise, absolute darkness, and pre-sleep wind-down are vital for deep sleep stages."
            }

            circadianWindow = when (chronotype) {
                Chronotype.BEAR -> "10:45 PM – 11:15 PM (Melatonin Peak Window)"
                Chronotype.LION -> "9:30 PM – 10:15 PM"
                Chronotype.WOLF -> "12:15 AM – 1:00 AM"
                Chronotype.DOLPHIN -> "11:30 PM – 12:00 AM"
            }

            caffeineCutoff = when (chronotype) {
                Chronotype.LION -> "1:30 PM (8h prior to sleep)"
                Chronotype.BEAR -> "2:30 PM (8h prior to sleep)"
                Chronotype.WOLF -> "4:30 PM (8h prior to sleep)"
                Chronotype.DOLPHIN -> "1:00 PM (Adenosine clearance)"
            }

            sleepDebtAnalysis = when {
                count == 0 -> "Baseline state based on your target goal of ${goal.targetCycles} cycles (${goal.targetCycles * 90 / 60}h). Start logging to unlock deeper pattern insights."
                diff >= 15 -> "Optimal sleep balance (+${diff}m surplus above goal). Cellular restoration and memory consolidation are running at peak efficiency."
                diff >= -30 -> "Well-regulated sleep balance with minor deficit (~${kotlin.math.abs(diff)}m). Standard 5-cycle rest tonight will easily restore equilibrium."
                else -> "Significant sleep debt detected (~${kotlin.math.abs(diff)}m below target). Aim for a restorative 6-cycle (9 hour) recovery rest tonight."
            }

            if (avgDeep < 80 && count > 0) {
                recommendations.add("Your deep sleep ratio is slightly below target; lower room temperature to 65°F (18°C) to facilitate slow-wave sleep transitions.")
            } else {
                recommendations.add("Dim all overhead blue-tinted lighting 45 minutes before sleep to trigger natural melatonin synthesis.")
            }

            if (chronotype == Chronotype.WOLF) {
                recommendations.add("Expose eyes to natural morning sunlight within 30 minutes of waking to anchor your circadian master clock.")
            } else {
                recommendations.add("Maintain a rigid wake-up time even on weekends to preserve sleep drive and circadian alignment.")
            }

            recommendations.add("Utilize the built-in 4-7-8 rhythmic breathing session in Somno Tracker to stimulate parasympathetic relaxation.")
            recommendations.add("Avoid heavy meals and high-sugar snacks within 2.5 hours of your intended bedtime.")

            quote = "\"Sleep is the golden chain that ties health and our bodies together.\" — Thomas Dekker"
        }

        AIAnalysisReport(
            chronotypeSummary = chronotypeSummary,
            circadianWindow = circadianWindow,
            caffeineCutoff = caffeineCutoff,
            sleepDebtAnalysis = sleepDebtAnalysis,
            recommendations = recommendations,
            motivationalQuote = quote
        )
    }
}

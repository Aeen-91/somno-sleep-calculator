package com.example.data.localization

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.LayoutDirection

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val layoutDirection: LayoutDirection
) {
    ENGLISH("en", "English", "English", LayoutDirection.Ltr),
    PERSIAN("fa", "Persian", "فارسی", LayoutDirection.Rtl)
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.ENGLISH }

object LocalizedStrings {

    fun formatDigits(text: String, language: AppLanguage, usePersianDigits: Boolean = true): String {
        if (language != AppLanguage.PERSIAN || !usePersianDigits) return text
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in text) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun formatMinutes(totalMinutes: Int, language: AppLanguage, usePersianDigits: Boolean = true): String {
        val hours = totalMinutes / 60
        val mins = totalMinutes % 60
        val text = if (language == AppLanguage.PERSIAN) {
            if (hours > 0 && mins > 0) "$hours ساعت و $mins دقیقه"
            else if (hours > 0) "$hours ساعت"
            else "$mins دقیقه"
        } else {
            if (hours > 0 && mins > 0) "${hours}h ${mins}m"
            else if (hours > 0) "${hours}h"
            else "${mins}m"
        }
        return formatDigits(text, language, usePersianDigits)
    }

    fun get(key: String, language: AppLanguage): String {
        val map = when (language) {
            AppLanguage.ENGLISH -> en
            AppLanguage.PERSIAN -> fa
        }
        return map[key] ?: en[key] ?: key
    }

    private val en = mapOf(
        "app_title" to "Somno",
        "app_tagline" to "AI Sleep Architecture & REM Calculator",
        "tab_calculator" to "Calculator",
        "tab_chart" to "REM Chart",
        "tab_tracker" to "Deep Tracker",
        "tab_coach" to "AI Coach",
        "tab_settings" to "Settings",

        // Calculator
        "calc_mode_wake_up" to "Wake Up At",
        "calc_mode_bedtime" to "Go to Bed At",
        "calc_mode_sleep_now" to "Sleep Now",
        "calc_header_wake_up" to "I want to wake up at:",
        "calc_header_bedtime" to "I plan to go to sleep at:",
        "calc_header_sleep_now" to "If you sleep right now",
        "calc_sleep_now_btn" to "SLEEP NOW",
        "calc_cycle_count" to "%d Cycles",
        "calc_sleep_latency_setting" to "Fall Asleep Latency: %d min",
        "calc_sleep_latency_note" to "(Average human takes ~14-15m to fall asleep)",
        "calc_optimal_badge" to "OPTIMAL REST",
        "calc_good_badge" to "GOOD REST",
        "calc_minimum_badge" to "MINIMUM REST",
        "calc_emergency_badge" to "POWER REST",
        "calc_set_alarm" to "Set Alarm",
        "calc_alarm_set_success" to "Alarm scheduled for %s",

        // Stages
        "stage_awake" to "Awake",
        "stage_rem" to "REM Sleep (Dreaming & Memory)",
        "stage_light" to "Light Sleep (Spindles & N2)",
        "stage_deep" to "Deep SWS (Physical Recovery & N3)",

        // Chart Screen
        "chart_title" to "REM Cycle & Hypnogram",
        "chart_subtitle" to "Ultradian 90-Minute Sleep Architecture",
        "chart_deep_science_title" to "Deep Sleep (Slow-Wave / N3)",
        "chart_deep_science_body" to "Deep sleep occurs predominantly in the first half of the night. Growth hormone is secreted, muscle tissues repair, metabolic waste is cleared by the glymphatic system, and cellular energy is restored.",
        "chart_rem_science_title" to "REM Sleep (Rapid Eye Movement)",
        "chart_rem_science_body" to "REM sleep dominates the second half of the night and lengthens in each successive cycle. It consolidates procedural memories, enhances neuroplasticity, synthesizes emotions, and fuels creativity.",
        "chart_science_title" to "Why 90-Minute Cycles Matter?",
        "chart_science_body" to "Waking in the middle of Stage 3 Deep Sleep triggers profound sleep inertia (grogginess and brain fog). Waking at the boundary of a 90-minute cycle ensures cortisol is rising naturally, leaving you alert and revitalized.",
        "chart_scrub_prompt" to "Tap along timeline to inspect stages & cycle progression",

        // Tracker Screen
        "tracker_title" to "Sleep & Deep Rest Calculator",
        "tracker_subtitle" to "Calculate cycles and deep sleep achieved",
        "tracker_bedtime_label" to "Went to bed at:",
        "tracker_waketime_label" to "Woke up at:",
        "tracker_calculate_btn" to "Calculate Sleep Achieved",
        "tracker_cycles_passed" to "Cycles Passed",
        "tracker_total_duration" to "Total Rest Duration",
        "tracker_deep_achieved" to "Est. Total Deep Sleep",
        "tracker_rem_achieved" to "Est. Total REM Sleep",
        "tracker_light_achieved" to "Est. Light Sleep",
        "tracker_efficiency" to "Sleep Efficiency",
        "tracker_quality_score" to "Sleep Quality Score",
        "tracker_sleep_debt" to "Sleep Debt vs Goal",
        "tracker_save_btn" to "Save to Sleep History",
        "tracker_history_title" to "Recent Sleep History",
        "tracker_no_logs" to "No sleep logs recorded yet. Track a night to see your trends!",
        "tracker_log_deleted" to "Sleep record deleted",
        "tracker_breathing_title" to "4-7-8 Relaxing Sleep Breather",
        "tracker_breathing_sub" to "Proven physiological technique to activate vagal parasympathetic tone",
        "tracker_start_breathing" to "Start Breathing Guide",
        "tracker_stop_breathing" to "Stop",
        "tracker_breathe_in" to "Inhale deeply through nose (4s)",
        "tracker_breathe_hold" to "Hold your breath calmly (7s)",
        "tracker_breathe_out" to "Exhale completely with gentle whoosh (8s)",

        // AI Coach Screen
        "ai_title" to "Personalized AI Sleep Analysis",
        "ai_subtitle" to "Local & Cloud Circadian Intelligence",
        "ai_chronotype_title" to "Your Circadian Chronotype",
        "ai_chronotype_bear" to "Bear (Solar Rhythm)",
        "ai_chronotype_bear_desc" to "Synchronized with sunrise and sunset. Peak energy in morning/midday.",
        "ai_chronotype_lion" to "Lion (Early Lark)",
        "ai_chronotype_lion_desc" to "Wakes at dawn with explosive morning focus, winds down early in the evening.",
        "ai_chronotype_wolf" to "Wolf (Night Owl)",
        "ai_chronotype_wolf_desc" to "Delayed circadian phase. Creative peaks in late evening and night.",
        "ai_chronotype_dolphin" to "Dolphin (Light Sleeper)",
        "ai_chronotype_dolphin_desc" to "High neural sensitivity. Needs quiet, pitch dark, cool environment.",
        "ai_btn_analyze" to "Generate AI Sleep Analysis",
        "ai_btn_reanalyze" to "Refresh AI Analysis",
        "ai_analyzing" to "AI analyzing sleep architecture & circadian rhythms...",
        "ai_quiz_title" to "Discover Your Chronotype (3 Questions)",
        "ai_goal_title" to "Personal Sleep Goal",
        "ai_target_cycles" to "%d Cycles (%s)",
        "ai_melatonin_window" to "Optimal Melatonin Window",
        "ai_caffeine_cutoff" to "Caffeine Cutoff Time",
        "ai_sleep_debt_advice" to "Sleep Debt & Recovery Speed",
        "ai_hygiene_tips" to "Circadian Optimization Protocols",

        // Settings Screen
        "settings_title" to "Settings & About",
        "settings_lang" to "App Language",
        "settings_lang_sub" to "Switch between English and Persian (فارسی)",
        "settings_persian_digits" to "Persian Numerals",
        "settings_persian_digits_sub" to "Display numbers in Persian format (۱۲۳ vs 123)",
        "settings_latency" to "Default Sleep Latency",
        "settings_latency_sub" to "Minutes to fall asleep",
        "settings_clear_data" to "Clear All Sleep History",
        "settings_clear_confirm" to "Are you sure you want to permanently delete all sleep logs?",
        "settings_confirm" to "Delete All",
        "settings_cancel" to "Cancel",
        "settings_data_cleared" to "All sleep records cleared",
        "settings_export_title" to "Export & Share Sleep Data",
        "settings_export_sub" to "Export your recorded sessions in JSON, CSV, or Summary Report",
        "settings_export_json" to "Share JSON Data",
        "settings_export_csv" to "Share CSV File",
        "settings_export_summary" to "Copy Summary Report",
        "settings_copied" to "Copied to clipboard!",
        "settings_about" to "About Somno",
        "settings_credit" to "Created by @Shibadev_Copy on Telegram",
        "settings_version" to "Somno v1.0.0 • Liquid Glass Edition"
    )

    private val fa = mapOf(
        "app_title" to "سُمنو (Somno)",
        "app_tagline" to "محاسبه‌گر پیشرفته چرخه‌های خواب و بهینه‌ساز REM با هوش مصنوعی",
        "tab_calculator" to "محاسبه‌گر",
        "tab_chart" to "نمودار REM",
        "tab_tracker" to "ردیاب عمیق",
        "tab_coach" to "مربی هوش مصنوعی",
        "tab_settings" to "تنظیمات",

        // Calculator
        "calc_mode_wake_up" to "زمان بیدار شدن",
        "calc_mode_bedtime" to "زمان رفتن به تخت",
        "calc_mode_sleep_now" to "خوابیدن در همین لحظه",
        "calc_header_wake_up" to "می‌خواهم در این ساعت بیدار شوم:",
        "calc_header_bedtime" to "می‌خواهم در این ساعت بخوابم:",
        "calc_header_sleep_now" to "اگر در همین لحظه به رختخواب بروید",
        "calc_sleep_now_btn" to "الان می‌خوابم",
        "calc_cycle_count" to "%d چرخه کامل",
        "calc_sleep_latency_setting" to "زمان به خواب رفتن: %d دقیقه",
        "calc_sleep_latency_note" to "(انسان‌ها به طور میانگین ۱۴ تا ۱۵ دقیقه طول می‌کشد تا به خواب بروند)",
        "calc_optimal_badge" to "خواب ایده‌آل و طلایی",
        "calc_good_badge" to "خواب خوب و مناسب",
        "calc_minimum_badge" to "حداقل میزان استراحت",
        "calc_emergency_badge" to "چرت نیروبخش",
        "calc_set_alarm" to "تنظیم آلارم",
        "calc_alarm_set_success" to "آلارم برای ساعت %s تنظیم شد",

        // Stages
        "stage_awake" to "بیداری و هشیاری",
        "stage_rem" to "خواب رویایی (REM)",
        "stage_light" to "خواب سبک (مرحله N2)",
        "stage_deep" to "خواب عمیق (ترمیم بدن N3)",

        // Chart Screen
        "chart_title" to "چرخه‌ها و معماری خواب (Hypnogram)",
        "chart_subtitle" to "ساختار اولترادین چرخه‌های ۹۰ دقیقه‌ای خواب",
        "chart_deep_science_title" to "خواب عمیق (موج‌آهسته SWS / N3)",
        "chart_deep_science_body" to "خواب عمیق عمدتا در نیمه اول شب رخ می‌دهد. در این مرحله هورمون رشد ترشح می‌شود، سلول‌ها و بافت‌های عضلانی ترمیم می‌گردند و سیستم گلیمفاتیک مغز را از سموم پاکسازی می‌کند.",
        "chart_rem_science_title" to "خواب رویایی REM (حرکات سریع چشم)",
        "chart_rem_science_body" to "فاز REM در نیمه دوم شب غالب است و در هر چرخه متوالی طولانی‌تر می‌شود. این مرحله برای تثبیت خاطرات، افزایش انعطاف‌پذیری عصبی، خلاقیت و تعادل هیجانی مغز حیاتی است.",
        "chart_science_title" to "چرا چرخه‌های ۹۰ دقیقه‌ای حیاتی هستند؟",
        "chart_science_body" to "بیدار شدن در میانه مرحله ۳ خواب عمیق موجب سستی شدید و سردرد صبحگاهی (اینرسی خواب) می‌شود. بیدار شدن در انتهای چرخه ۹۰ دقیقه‌ای باعث می‌شود هورمون کورتیزول به طور طبیعی ترشح شده و با انرژی و شادابی کامل روز را آغاز کنید.",
        "chart_scrub_prompt" to "روی خط زمان لمس کنید تا مشخصات هر مرحله را ببینید",

        // Tracker Screen
        "tracker_title" to "محاسبه‌گر خواب انجام شده و خواب عمیق",
        "tracker_subtitle" to "محاسبه دقیق چرخه‌های سپری شده و میزان خواب عمیق",
        "tracker_bedtime_label" to "ساعت رفتن به رختخواب:",
        "tracker_waketime_label" to "ساعت بیدار شدن:",
        "tracker_calculate_btn" to "محاسبه کیفیت و چرخه‌ها",
        "tracker_cycles_passed" to "چرخه‌های سپری شده",
        "tracker_total_duration" to "مدت زمان خواب",
        "tracker_deep_achieved" to "خواب عمیق ثبت شده",
        "tracker_rem_achieved" to "خواب رویایی REM ثبت شده",
        "tracker_light_achieved" to "خواب سبک",
        "tracker_efficiency" to "راندمان خواب",
        "tracker_quality_score" to "امتیاز کیفیت خواب",
        "tracker_sleep_debt" to "کسری/مازاد خواب نسبت به هدف",
        "tracker_save_btn" to "ثبت در تاریخچه خواب",
        "tracker_history_title" to "تاریخچه خواب‌های اخیر",
        "tracker_no_logs" to "هنوز خوابی ثبت نشده است. پس از بیداری خوابتان را ثبت کنید!",
        "tracker_log_deleted" to "گزارش خواب حذف شد",
        "tracker_breathing_title" to "تنفس ریلکسیشن ۴-۷-۸ قبل از خواب",
        "tracker_breathing_sub" to "تکنیک تنفسی اثبات‌شده علمی جهت کاهش ضربان قلب و تسریع خواب",
        "tracker_start_breathing" to "شروع تمرین تنفس",
        "tracker_stop_breathing" to "توقف",
        "tracker_breathe_in" to "دم عمیق از راه بینی (۴ ثانیه)",
        "tracker_breathe_hold" to "حبس نفس در آرامش (۷ ثانیه)",
        "tracker_breathe_out" to "بازدم آرام از دهان (۸ ثانیه)",

        // AI Coach Screen
        "ai_title" to "تحلیل هوشمند خواب با مربی اختصاصی AI",
        "ai_subtitle" to "تحلیل محلی و پیشرفته الگوی شبانه‌روزی و بیولوژیک",
        "ai_chronotype_title" to "تیپ بدنی و ریتم زیستی شما (Chronotype)",
        "ai_chronotype_bear" to "خرس (ریتم خورشیدی)",
        "ai_chronotype_bear_desc" to "هماهنگ با طلوع و غروب آفتاب. اوج انرژی در ساعات ۱۰ صبح تا ۲ بعدازظهر.",
        "ai_chronotype_lion" to "شیر (سحرخیز)",
        "ai_chronotype_lion_desc" to "بیداری با طلوع آفتاب، انرژی فوق‌العاده در صبح زود، نیاز به خواب زودهنگام در شب.",
        "ai_chronotype_wolf" to "گرگ (شب‌بیدار)",
        "ai_chronotype_wolf_desc" to "تاخیر در فاز شبانه‌روزی. اوج تمرکز ذهنی و خلاقیت در ساعات عصر و شب.",
        "ai_chronotype_dolphin" to "دلفین (خواب حساس)",
        "ai_chronotype_dolphin_desc" to "هوشیاری عصبی بالا در خواب. نیاز به تاریکی مطلق، سکوت و دمای خنک.",
        "ai_btn_analyze" to "دریافت تحلیل خواب با هوش مصنوعی",
        "ai_btn_reanalyze" to "بروزرسانی تحلیل مربی AI",
        "ai_analyzing" to "هوش مصنوعی در حال تحلیل چرخه‌ها و معماری خواب شماست...",
        "ai_quiz_title" to "آزمون ۳ سوالی تشخیص کرونوتایپ",
        "ai_goal_title" to "هدف روزانه استراحت",
        "ai_target_cycles" to "%d چرخه خواب (%s)",
        "ai_melatonin_window" to "پنجره طلایی ترشح ملاتونین",
        "ai_caffeine_cutoff" to "آخرین مهلت مصرف کافئین",
        "ai_sleep_debt_advice" to "ارزیابی بدهی خواب و جبران",
        "ai_hygiene_tips" to "پروتکل‌های بهینه‌سازی خواب",

        // Settings Screen
        "settings_title" to "تنظیمات و درباره برنامه",
        "settings_lang" to "زبان برنامه (Language)",
        "settings_lang_sub" to "تغییر بین زبان‌های فارسی و انگلیسی",
        "settings_persian_digits" to "نمایش اعداد فارسی",
        "settings_persian_digits_sub" to "نمایش ارقام به صورت فارسی (۱۲۳ به جای 123)",
        "settings_latency" to "زمان میانگین به خواب رفتن",
        "settings_latency_sub" to "دقایق لازم تا به خواب رفتن",
        "settings_clear_data" to "پاکسازی تمام سوابق خواب",
        "settings_clear_confirm" to "آیا از پاک کردن تمام رکوردهای خواب ذخیره شده مطمئن هستید؟",
        "settings_confirm" to "حذف همه",
        "settings_cancel" to "انصراف",
        "settings_data_cleared" to "تمام سوابق خواب پاک شدند",
        "settings_export_title" to "خروجی و اشتراک‌گذاری داده‌ها",
        "settings_export_sub" to "دریافت خروجی از خواب‌های ثبت شده به صورت JSON، فایل اکسل CSV یا گزارش متنی",
        "settings_export_json" to "خروجی JSON",
        "settings_export_csv" to "خروجی فایل CSV",
        "settings_export_summary" to "کپی گزارش جامع",
        "settings_copied" to "در حافظه کلیپ‌بورد کپی شد!",
        "settings_about" to "درباره سُمنو (Somno)",
        "settings_credit" to "ساخته شده توسط @Shibadev_Copy در تلگرام",
        "settings_version" to "نسخه ۱.۰.۰ سُمنو • طراحی متریال مایع و شیشه‌ای (Liquid Glass)"
    )
}

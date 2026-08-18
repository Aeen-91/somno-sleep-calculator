package com.example.data.export

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.data.db.SleepLogEntity
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.data.model.SleepGoal
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SleepDataExporter {

    fun exportToJson(logs: List<SleepLogEntity>, goal: SleepGoal): String {
        val root = JSONObject()
        root.put("app", "Somno")
        root.put("version", "1.0.0")
        root.put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
        root.put("creator", "@Shibadev_Copy on Telegram")

        val goalObj = JSONObject().apply {
            put("targetCycles", goal.targetCycles)
            put("targetDurationHours", goal.targetCycles * 1.5)
            put("chronotype", goal.chronotype.name)
        }
        root.put("userSleepGoal", goalObj)

        val logsArray = JSONArray()
        logs.forEach { log ->
            val logObj = JSONObject().apply {
                put("id", log.id)
                put("timestamp", log.timestamp)
                put("formattedDate", SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(log.timestamp)))
                put("bedTime", String.format(Locale.US, "%02d:%02d", log.bedHour, log.bedMinute))
                put("wakeTime", String.format(Locale.US, "%02d:%02d", log.wakeHour, log.wakeMinute))
                put("totalDurationMinutes", log.totalDurationMinutes)
                put("totalDurationHours", String.format(Locale.US, "%.2f", log.totalDurationMinutes / 60.0))
                put("cyclesCompleted", log.cyclesCount)
                put("deepSleepMinutes", log.deepSleepMinutes)
                put("remMinutes", log.remMinutes)
                put("lightMinutes", log.lightMinutes)
                put("qualityScore", log.qualityScore)
                put("efficiencyPercentage", log.efficiency)
            }
            logsArray.put(logObj)
        }
        root.put("sleepLogs", logsArray)
        root.put("totalRecordedLogs", logs.size)

        return root.toString(2)
    }

    fun exportToCsv(logs: List<SleepLogEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,Date,BedTime,WakeTime,DurationMinutes,DurationHours,Cycles,DeepSleepMin,RemMin,LightMin,QualityScore,Efficiency\n")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        logs.forEach { log ->
            val dateStr = dateFormat.format(Date(log.timestamp))
            val bedTime = String.format(Locale.US, "%02d:%02d", log.bedHour, log.bedMinute)
            val wakeTime = String.format(Locale.US, "%02d:%02d", log.wakeHour, log.wakeMinute)
            val hours = String.format(Locale.US, "%.2f", log.totalDurationMinutes / 60.0)
            sb.append("${log.id},$dateStr,$bedTime,$wakeTime,${log.totalDurationMinutes},$hours,${log.cyclesCount},${log.deepSleepMinutes},${log.remMinutes},${log.lightMinutes},${log.qualityScore},${log.efficiency}%\n")
        }
        return sb.toString()
    }

    fun exportToSummaryText(logs: List<SleepLogEntity>, goal: SleepGoal, lang: AppLanguage): String {
        val isFa = lang == AppLanguage.PERSIAN
        val count = logs.size
        val avgDuration = if (count > 0) logs.map { it.totalDurationMinutes }.average().toInt() else 0
        val avgScore = if (count > 0) logs.map { it.qualityScore }.average().toInt() else 0
        val avgDeep = if (count > 0) logs.map { it.deepSleepMinutes }.average().toInt() else 0
        val avgCycles = if (count > 0) String.format(Locale.US, "%.1f", logs.map { it.cyclesCount }.average()) else "0.0"

        val sb = StringBuilder()
        if (isFa) {
            sb.append("📊 گزارش جامع خواب اپلیکیشن سمنو (Somno)\n")
            sb.append("سازنده: @Shibadev_Copy\n")
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            sb.append("تیپ زیستی (کرونوتایپ): ${LocalizedStrings.get(goal.chronotype.titleKey, lang)}\n")
            sb.append("هدف خواب: ${goal.targetCycles} چرخه (${goal.targetCycles * 90 / 60} ساعت)\n")
            sb.append("تعداد شب‌های ثبت شده: $count\n")
            sb.append("میانگین مدت خواب: ${avgDuration / 60} ساعت و ${avgDuration % 60} دقیقه\n")
            sb.append("میانگین چرخه‌های کامل: $avgCycles چرخه\n")
            sb.append("میانگین خواب عمیق: ${avgDeep / 60} ساعت و ${avgDeep % 60} دقیقه\n")
            sb.append("امتیاز میانگین کیفیت: $avgScore از ۱۰۰\n\n")
            sb.append("سوابق اخیر:\n")
            logs.take(5).forEach { log ->
                val date = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(log.timestamp))
                sb.append("• $date: ساعت ${String.format(Locale.US, "%02d:%02d", log.bedHour, log.bedMinute)} تا ${String.format(Locale.US, "%02d:%02d", log.wakeHour, log.wakeMinute)} (${log.totalDurationMinutes / 60}h ${log.totalDurationMinutes % 60}m) - امتیاز: ${log.qualityScore}\n")
            }
        } else {
            sb.append("📊 Somno Comprehensive Sleep Report\n")
            sb.append("Created by @Shibadev_Copy\n")
            sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n")
            sb.append("Chronotype: ${LocalizedStrings.get(goal.chronotype.titleKey, lang)}\n")
            sb.append("Target Goal: ${goal.targetCycles} cycles (${goal.targetCycles * 90 / 60}h)\n")
            sb.append("Logged Nights: $count\n")
            sb.append("Average Duration: ${avgDuration / 60}h ${avgDuration % 60}m\n")
            sb.append("Average Cycles: $avgCycles cycles\n")
            sb.append("Average Deep Sleep: ${avgDeep / 60}h ${avgDeep % 60}m\n")
            sb.append("Average Quality Score: $avgScore / 100\n\n")
            sb.append("Recent Sleep Sessions:\n")
            logs.take(5).forEach { log ->
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(log.timestamp))
                sb.append("• $date: ${String.format(Locale.US, "%02d:%02d", log.bedHour, log.bedMinute)} to ${String.format(Locale.US, "%02d:%02d", log.wakeHour, log.wakeMinute)} (${log.totalDurationMinutes / 60}h ${log.totalDurationMinutes % 60}m) - Score: ${log.qualityScore}\n")
            }
        }
        return sb.toString()
    }

    fun shareContent(context: Context, content: String, title: String, mimeType: String = "text/plain") {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, content)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val chooser = Intent.createChooser(intent, title).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Sharing failed", Toast.LENGTH_SHORT).show()
        }
    }

    fun copyToClipboard(context: Context, text: String, label: String, feedbackMessage: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, feedbackMessage, Toast.LENGTH_SHORT).show()
    }
}

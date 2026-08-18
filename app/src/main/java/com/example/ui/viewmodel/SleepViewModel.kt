package com.example.ui.viewmodel

import android.app.Application
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiSleepService
import com.example.data.calculator.SleepCalculatorEngine
import com.example.data.db.SleepDao
import com.example.data.db.SleepLogEntity
import com.example.data.db.SleepRepository
import com.example.data.db.SomnoDatabase
import com.example.data.localization.AppLanguage
import com.example.data.localization.LocalizedStrings
import com.example.data.model.AIAnalysisReport
import com.example.data.model.CalculationMode
import com.example.data.model.Chronotype
import com.example.data.model.HypnogramPoint
import com.example.data.model.SleepCalculationResult
import com.example.data.model.SleepGoal
import com.example.data.model.SleepRecommendation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class SleepViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SomnoDatabase.getDatabase(application)
    private val repository = SleepRepository(db.sleepDao(), application)
    private val geminiService = GeminiSleepService()

    // Navigation Tab
    val currentTab = MutableStateFlow(0)

    // Language & Preferences
    val appLanguage: StateFlow<AppLanguage> = repository.appLanguage
    val usePersianDigits: StateFlow<Boolean> = repository.usePersianDigits
    val sleepLatency: StateFlow<Int> = repository.sleepLatencyMinutes
    val sleepLatencyMinutes: StateFlow<Int> = repository.sleepLatencyMinutes
    val sleepGoal: StateFlow<SleepGoal> = repository.sleepGoal

    // Calculator State
    val calculationMode = MutableStateFlow(CalculationMode.WAKE_UP)
    val targetHour = MutableStateFlow(7)
    val targetMinute = MutableStateFlow(0)
    val recommendations = MutableStateFlow<List<SleepRecommendation>>(emptyList())

    // Cycle Chart State
    val hypnogramPoints = MutableStateFlow<List<HypnogramPoint>>(emptyList())
    val selectedChartCycle = MutableStateFlow(5)
    val scrubbedMinute = MutableStateFlow<Int?>(null)

    // Tracker / Deep Sleep Log State
    val trackerBedHour = MutableStateFlow(23)
    val trackerBedMinute = MutableStateFlow(30)
    val trackerWakeHour = MutableStateFlow(7)
    val trackerWakeMinute = MutableStateFlow(15)
    val trackerResult = MutableStateFlow<SleepCalculationResult?>(null)

    // Breathing relaxation state
    val isBreathingActive = MutableStateFlow(false)
    val breathingPhase = MutableStateFlow("tracker_breathe_in") // in (4s), hold (7s), out (8s)
    val breathingSecondsLeft = MutableStateFlow(4)
    private var breathingJob: Job? = null

    // Room Database Logs
    val allLogs: StateFlow<List<SleepLogEntity>> = repository.getAllLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Sleep Coach State
    val isAnalyzingAi = MutableStateFlow(false)
    val aiAnalysisReport = MutableStateFlow<AIAnalysisReport?>(null)

    // Chronotype Quiz state
    val quizSelectedOption1 = MutableStateFlow<Int?>(null)
    val quizSelectedOption2 = MutableStateFlow<Int?>(null)
    val quizSelectedOption3 = MutableStateFlow<Int?>(null)

    init {
        updateRecommendations()
        updateHypnogram()
        calculateTrackerSleep()
    }

    fun setTab(index: Int) {
        currentTab.value = index
    }

    fun setCalculationMode(mode: CalculationMode) {
        calculationMode.value = mode
        if (mode == CalculationMode.SLEEP_NOW) {
            val calendar = Calendar.getInstance()
            targetHour.value = calendar.get(Calendar.HOUR_OF_DAY)
            targetMinute.value = calendar.get(Calendar.MINUTE)
        } else if (mode == CalculationMode.BEDTIME && targetHour.value == 7) {
            targetHour.value = 23
            targetMinute.value = 0
        } else if (mode == CalculationMode.WAKE_UP && targetHour.value == 23) {
            targetHour.value = 7
            targetMinute.value = 0
        }
        updateRecommendations()
    }

    fun setSleepNow() {
        setCalculationMode(CalculationMode.SLEEP_NOW)
    }

    fun setTargetTime(hour: Int, minute: Int) {
        targetHour.value = hour
        targetMinute.value = minute
        updateRecommendations()
    }

    fun updateRecommendations() {
        val latency = sleepLatency.value
        val list = when (calculationMode.value) {
            CalculationMode.WAKE_UP -> {
                SleepCalculatorEngine.calculateBedtimes(targetHour.value, targetMinute.value, latency)
            }
            CalculationMode.BEDTIME -> {
                SleepCalculatorEngine.calculateWakeTimes(targetHour.value, targetMinute.value, latency)
            }
            CalculationMode.SLEEP_NOW -> {
                val cal = Calendar.getInstance()
                SleepCalculatorEngine.calculateSleepNowTimes(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), latency)
            }
        }
        recommendations.value = list
    }

    fun updateHypnogram() {
        hypnogramPoints.value = SleepCalculatorEngine.generateHypnogramStages(
            totalCycles = selectedChartCycle.value,
            latencyMinutes = sleepLatency.value
        )
    }

    fun selectChartCycle(cycles: Int) {
        selectedChartCycle.value = cycles
        updateHypnogram()
    }

    fun setScrubbedMinute(minute: Int?) {
        scrubbedMinute.value = minute
    }

    fun setTrackerTimes(bedH: Int, bedM: Int, wakeH: Int, wakeM: Int) {
        trackerBedHour.value = bedH
        trackerBedMinute.value = bedM
        trackerWakeHour.value = wakeH
        trackerWakeMinute.value = wakeM
        calculateTrackerSleep()
    }

    fun calculateTrackerSleep() {
        val latency = sleepLatency.value
        val goalMinutes = sleepGoal.value.targetCycles * 90
        trackerResult.value = SleepCalculatorEngine.calculateAchievedSleep(
            bedHour = trackerBedHour.value,
            bedMinute = trackerBedMinute.value,
            wakeHour = trackerWakeHour.value,
            wakeMinute = trackerWakeMinute.value,
            latencyMinutes = latency,
            targetGoalMinutes = goalMinutes
        )
    }

    fun saveCurrentTrackerSleep() {
        val result = trackerResult.value ?: return
        viewModelScope.launch {
            val entity = SleepLogEntity(
                bedHour = result.bedHour,
                bedMinute = result.bedMinute,
                wakeHour = result.wakeHour,
                wakeMinute = result.wakeMinute,
                totalDurationMinutes = result.totalMinutes,
                cyclesCount = result.cyclesCount,
                deepSleepMinutes = result.deepSleepMinutes,
                remMinutes = result.remMinutes,
                lightMinutes = result.lightMinutes,
                qualityScore = result.qualityScore,
                efficiency = result.sleepEfficiency
            )
            repository.saveSleepLog(entity)
            val lang = appLanguage.value
            Toast.makeText(
                getApplication(),
                if (lang == AppLanguage.PERSIAN) "خواب شما با موفقیت ثبت شد" else "Sleep log saved successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch {
            repository.deleteSleepLog(id)
            val lang = appLanguage.value
            Toast.makeText(
                getApplication(),
                LocalizedStrings.get("tracker_log_deleted", lang),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deleteLog(log: SleepLogEntity) {
        viewModelScope.launch {
            repository.deleteSleepLog(log)
            val lang = appLanguage.value
            Toast.makeText(
                getApplication(),
                LocalizedStrings.get("tracker_log_deleted", lang),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAllLogs()
            val lang = appLanguage.value
            Toast.makeText(
                getApplication(),
                LocalizedStrings.get("settings_data_cleared", lang),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun clearAllHistory() {
        clearAllLogs()
    }

    fun setAlarmForTime(hour: Int, minute: Int) {
        try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, "Somno Sleep Alarm")
                putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            val lang = appLanguage.value
            val timeFormatted = String.format(Locale.US, "%02d:%02d", hour, minute)
            val msg = String.format(LocalizedStrings.get("calc_alarm_set_success", lang), timeFormatted)
            Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleBreather() {
        if (isBreathingActive.value) {
            breathingJob?.cancel()
            isBreathingActive.value = false
        } else {
            isBreathingActive.value = true
            breathingJob = viewModelScope.launch {
                while (isBreathingActive.value) {
                    // Inhale 4s
                    breathingPhase.value = "tracker_breathe_in"
                    for (i in 4 downTo 1) {
                        breathingSecondsLeft.value = i
                        delay(1000)
                    }
                    // Hold 7s
                    breathingPhase.value = "tracker_breathe_hold"
                    for (i in 7 downTo 1) {
                        breathingSecondsLeft.value = i
                        delay(1000)
                    }
                    // Exhale 8s
                    breathingPhase.value = "tracker_breathe_out"
                    for (i in 8 downTo 1) {
                        breathingSecondsLeft.value = i
                        delay(1000)
                    }
                }
            }
        }
    }

    fun requestAiAnalysis() {
        viewModelScope.launch {
            isAnalyzingAi.value = true
            try {
                val report = geminiService.generateSleepAnalysis(
                    chronotype = sleepGoal.value.chronotype,
                    goal = sleepGoal.value,
                    recentLogs = allLogs.value,
                    language = appLanguage.value
                )
                aiAnalysisReport.value = report
            } catch (e: Exception) {
                // Ignore failure
            } finally {
                isAnalyzingAi.value = false
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        repository.setLanguage(lang)
        updateRecommendations()
        if (aiAnalysisReport.value != null) {
            requestAiAnalysis()
        }
    }

    fun setUsePersianDigits(enabled: Boolean) {
        repository.setUsePersianDigits(enabled)
    }

    fun setSleepLatency(minutes: Int) {
        repository.setSleepLatencyMinutes(minutes)
        updateRecommendations()
        updateHypnogram()
        calculateTrackerSleep()
    }

    fun setChronotype(chronotype: Chronotype) {
        repository.setChronotype(chronotype)
        requestAiAnalysis()
    }

    fun setGoalCycles(cycles: Int) {
        repository.setGoalCycles(cycles)
        calculateTrackerSleep()
    }

    fun submitQuizAnswer(questionIndex: Int, optionIndex: Int) {
        when (questionIndex) {
            1 -> quizSelectedOption1.value = optionIndex
            2 -> quizSelectedOption2.value = optionIndex
            3 -> quizSelectedOption3.value = optionIndex
        }

        val a1 = quizSelectedOption1.value
        val a2 = quizSelectedOption2.value
        val a3 = quizSelectedOption3.value

        if (a1 != null && a2 != null && a3 != null) {
            val determined = when {
                a1 == 0 || (a1 == 1 && a2 == 0) -> Chronotype.LION
                a1 == 2 || a2 == 2 -> Chronotype.WOLF
                a3 == 3 || a1 == 3 -> Chronotype.DOLPHIN
                else -> Chronotype.BEAR
            }
            setChronotype(determined)
        }
    }
}

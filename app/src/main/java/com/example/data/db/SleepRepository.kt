package com.example.data.db

import android.content.Context
import android.content.SharedPreferences
import com.example.data.localization.AppLanguage
import com.example.data.model.Chronotype
import com.example.data.model.SleepGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SleepRepository(
    private val sleepDao: SleepDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("somno_preferences", Context.MODE_PRIVATE)

    private val _appLanguage = MutableStateFlow(
        try {
            AppLanguage.valueOf(prefs.getString("pref_language", AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name)
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    )
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _usePersianDigits = MutableStateFlow(
        prefs.getBoolean("pref_persian_digits", true)
    )
    val usePersianDigits: StateFlow<Boolean> = _usePersianDigits.asStateFlow()

    private val _sleepLatencyMinutes = MutableStateFlow(
        prefs.getInt("pref_latency_minutes", 14)
    )
    val sleepLatencyMinutes: StateFlow<Int> = _sleepLatencyMinutes.asStateFlow()

    private val _sleepGoal = MutableStateFlow(
        SleepGoal(
            targetCycles = prefs.getInt("pref_goal_cycles", 5),
            chronotype = try {
                Chronotype.valueOf(prefs.getString("pref_chronotype", Chronotype.BEAR.name) ?: Chronotype.BEAR.name)
            } catch (e: Exception) {
                Chronotype.BEAR
            }
        )
    )
    val sleepGoal: StateFlow<SleepGoal> = _sleepGoal.asStateFlow()

    fun getAllLogs(): Flow<List<SleepLogEntity>> = sleepDao.getAllLogs()

    suspend fun saveSleepLog(log: SleepLogEntity): Long = sleepDao.insertLog(log)

    suspend fun deleteSleepLog(id: Long) = sleepDao.deleteById(id)

    suspend fun deleteSleepLog(log: SleepLogEntity) = sleepDao.deleteLog(log)

    suspend fun clearAllLogs() = sleepDao.deleteAllLogs()

    fun setLanguage(lang: AppLanguage) {
        prefs.edit().putString("pref_language", lang.name).apply()
        _appLanguage.value = lang
    }

    fun setUsePersianDigits(enabled: Boolean) {
        prefs.edit().putBoolean("pref_persian_digits", enabled).apply()
        _usePersianDigits.value = enabled
    }

    fun setSleepLatencyMinutes(minutes: Int) {
        prefs.edit().putInt("pref_latency_minutes", minutes).apply()
        _sleepLatencyMinutes.value = minutes
    }

    fun setChronotype(chronotype: Chronotype) {
        val updated = _sleepGoal.value.copy(chronotype = chronotype)
        prefs.edit().putString("pref_chronotype", chronotype.name).apply()
        _sleepGoal.value = updated
    }

    fun setGoalCycles(cycles: Int) {
        val updated = _sleepGoal.value.copy(targetCycles = cycles)
        prefs.edit().putInt("pref_goal_cycles", cycles).apply()
        _sleepGoal.value = updated
    }
}

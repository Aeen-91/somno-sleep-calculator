package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val bedHour: Int,
    val bedMinute: Int,
    val wakeHour: Int,
    val wakeMinute: Int,
    val totalDurationMinutes: Int,
    val cyclesCount: Double,
    val deepSleepMinutes: Int,
    val remMinutes: Int,
    val lightMinutes: Int,
    val qualityScore: Int,
    val efficiency: Int
)

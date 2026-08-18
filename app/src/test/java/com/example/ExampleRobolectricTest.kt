package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.calculator.SleepCalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Somno", appName)
  }

  @Test
  fun `verify sleep calculator 90 minute cycles`() {
    val recs = SleepCalculatorEngine.calculateBedtimes(7, 0, 15)
    assertEquals(4, recs.size)
    val optimalRec = recs.first { it.cycles == 5 }
    assertEquals(450, optimalRec.totalSleepMinutes)
    assertTrue(optimalRec.estimatedDeepSleepMinutes > 0)
    assertTrue(optimalRec.estimatedRemMinutes > 0)
  }

  @Test
  fun `verify past sleep analysis`() {
    val result = SleepCalculatorEngine.calculateAchievedSleep(23, 30, 7, 0, 15)
    assertTrue(result.cyclesCount > 4.0)
    assertTrue(result.qualityScore in 30..100)
    assertTrue(result.deepSleepMinutes > 0)
  }

  @Test
  fun `verify json and csv export functionality`() {
    val sampleLog = com.example.data.db.SleepLogEntity(
        id = 1,
        timestamp = System.currentTimeMillis(),
        bedHour = 23,
        bedMinute = 0,
        wakeHour = 7,
        wakeMinute = 0,
        totalDurationMinutes = 480,
        cyclesCount = 5.3,
        deepSleepMinutes = 110,
        remMinutes = 115,
        lightMinutes = 255,
        qualityScore = 92,
        efficiency = 95
    )
    val goal = com.example.data.model.SleepGoal(
        targetCycles = 5,
        chronotype = com.example.data.model.Chronotype.BEAR
    )
    val json = com.example.data.export.SleepDataExporter.exportToJson(listOf(sampleLog), goal)
    assertTrue(json.contains("\"app\": \"Somno\""))
    assertTrue(json.contains("\"totalRecordedLogs\": 1"))
    assertTrue(json.contains("\"qualityScore\": 92"))

    val csv = com.example.data.export.SleepDataExporter.exportToCsv(listOf(sampleLog))
    assertTrue(csv.contains("ID,Date,BedTime,WakeTime"))
    assertTrue(csv.contains("23:00,07:00,480"))
  }
}

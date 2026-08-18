package com.example.data.calculator

import com.example.data.model.HypnogramPoint
import com.example.data.model.SleepCalculationResult
import com.example.data.model.SleepQualityRating
import com.example.data.model.SleepRecommendation
import com.example.data.model.SleepStage
import java.util.Calendar

object SleepCalculatorEngine {

    const val CYCLE_MINUTES = 90

    fun calculateBedtimes(
        wakeHour: Int,
        wakeMinute: Int,
        latencyMinutes: Int = 15
    ): List<SleepRecommendation> {
        val cycles = listOf(6, 5, 4, 3)
        return cycles.map { cycleCount ->
            val totalSleepMin = cycleCount * CYCLE_MINUTES
            val totalOffsetMin = totalSleepMin + latencyMinutes

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, wakeHour)
                set(Calendar.MINUTE, wakeMinute)
                set(Calendar.SECOND, 0)
                add(Calendar.MINUTE, -totalOffsetMin)
            }

            val bedH = cal.get(Calendar.HOUR_OF_DAY)
            val bedM = cal.get(Calendar.MINUTE)

            val isOptimal = (cycleCount == 5 || cycleCount == 6)
            val rating = when (cycleCount) {
                6 -> SleepQualityRating.OPTIMAL
                5 -> SleepQualityRating.OPTIMAL
                4 -> SleepQualityRating.GOOD
                else -> SleepQualityRating.MINIMUM
            }

            val deepSleepMin = (totalSleepMin * 0.23).toInt()
            val remSleepMin = (totalSleepMin * 0.22).toInt()

            SleepRecommendation(
                cycles = cycleCount,
                totalSleepMinutes = totalSleepMin,
                targetHour = bedH,
                targetMinute = bedM,
                isRecommended = (cycleCount == 5),
                qualityRating = rating,
                estimatedDeepSleepMinutes = deepSleepMin,
                estimatedRemMinutes = remSleepMin
            )
        }
    }

    fun calculateWakeTimes(
        bedHour: Int,
        bedMinute: Int,
        latencyMinutes: Int = 15
    ): List<SleepRecommendation> {
        val cycles = listOf(3, 4, 5, 6)
        return cycles.reversed().map { cycleCount ->
            val totalSleepMin = cycleCount * CYCLE_MINUTES
            val totalOffsetMin = totalSleepMin + latencyMinutes

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, bedHour)
                set(Calendar.MINUTE, bedMinute)
                set(Calendar.SECOND, 0)
                add(Calendar.MINUTE, totalOffsetMin)
            }

            val wakeH = cal.get(Calendar.HOUR_OF_DAY)
            val wakeM = cal.get(Calendar.MINUTE)

            val rating = when (cycleCount) {
                6 -> SleepQualityRating.OPTIMAL
                5 -> SleepQualityRating.OPTIMAL
                4 -> SleepQualityRating.GOOD
                else -> SleepQualityRating.MINIMUM
            }

            val deepSleepMin = (totalSleepMin * 0.23).toInt()
            val remSleepMin = (totalSleepMin * 0.22).toInt()

            SleepRecommendation(
                cycles = cycleCount,
                totalSleepMinutes = totalSleepMin,
                targetHour = wakeH,
                targetMinute = wakeM,
                isRecommended = (cycleCount == 5),
                qualityRating = rating,
                estimatedDeepSleepMinutes = deepSleepMin,
                estimatedRemMinutes = remSleepMin
            )
        }
    }

    fun calculateSleepNowTimes(
        nowHour: Int,
        nowMinute: Int,
        latencyMinutes: Int = 15
    ): List<SleepRecommendation> {
        return calculateWakeTimes(nowHour, nowMinute, latencyMinutes)
    }

    fun calculateAchievedSleep(
        bedHour: Int,
        bedMinute: Int,
        wakeHour: Int,
        wakeMinute: Int,
        latencyMinutes: Int = 15,
        targetGoalMinutes: Int = 450
    ): SleepCalculationResult {
        var totalMinutes = (wakeHour * 60 + wakeMinute) - (bedHour * 60 + bedMinute)
        if (totalMinutes <= 0) {
            totalMinutes += 24 * 60
        }

        val netSleepMinutes = (totalMinutes - latencyMinutes).coerceAtLeast(30)
        val cyclesCount = Math.round((netSleepMinutes.toDouble() / CYCLE_MINUTES) * 10.0) / 10.0

        val deepSleepMinutes = (netSleepMinutes * 0.22).toInt().coerceAtLeast(15)
        val remMinutes = (netSleepMinutes * 0.21).toInt().coerceAtLeast(10)
        val lightMinutes = (netSleepMinutes - deepSleepMinutes - remMinutes).coerceAtLeast(5)

        val sleepEfficiency = ((netSleepMinutes.toDouble() / totalMinutes.toDouble()) * 100).toInt().coerceIn(60, 98)

        // Quality score computation
        val cycleRemainder = netSleepMinutes % 90
        val midCyclePenalty = if (cycleRemainder in 25..65) 12 else 0
        val durationScore = when {
            netSleepMinutes in 420..540 -> 40
            netSleepMinutes in 330..419 -> 32
            netSleepMinutes > 540 -> 35
            else -> (netSleepMinutes / 12).coerceIn(10, 30)
        }
        val effScore = (sleepEfficiency * 0.4).toInt()
        val qualityScore = (durationScore + effScore + 20 - midCyclePenalty).coerceIn(30, 99)

        val debtMinutes = targetGoalMinutes - netSleepMinutes

        return SleepCalculationResult(
            bedHour = bedHour,
            bedMinute = bedMinute,
            wakeHour = wakeHour,
            wakeMinute = wakeMinute,
            totalMinutes = totalMinutes,
            cyclesCount = cyclesCount,
            deepSleepMinutes = deepSleepMinutes,
            remMinutes = remMinutes,
            lightMinutes = lightMinutes,
            qualityScore = qualityScore,
            sleepEfficiency = sleepEfficiency,
            sleepDebtMinutes = debtMinutes
        )
    }

    fun generateHypnogramStages(
        totalCycles: Int = 5,
        latencyMinutes: Int = 15
    ): List<HypnogramPoint> {
        val points = mutableListOf<HypnogramPoint>()
        var offset = 0

        // Initial latency (Awake)
        points.add(HypnogramPoint(offset, 0, SleepStage.AWAKE, latencyMinutes))
        offset += latencyMinutes

        for (cycle in 1..totalCycles) {
            val progress = cycle.toFloat() / totalCycles.toFloat()
            // Early cycles have longer deep sleep; later cycles have longer REM
            val deepDuration = ((36 - (progress * 22)).toInt()).coerceIn(10, 38)
            val remDuration = ((12 + (progress * 26)).toInt()).coerceIn(12, 38)
            val lightDuration = (CYCLE_MINUTES - deepDuration - remDuration).coerceAtLeast(20)

            val light1 = lightDuration / 2
            val light2 = lightDuration - light1

            // Stage 1: Light Sleep
            points.add(HypnogramPoint(offset, cycle, SleepStage.LIGHT, light1))
            offset += light1

            // Stage 2: Deep SWS Sleep
            points.add(HypnogramPoint(offset, cycle, SleepStage.DEEP, deepDuration))
            offset += deepDuration

            // Stage 3: Light Sleep return
            points.add(HypnogramPoint(offset, cycle, SleepStage.LIGHT, light2))
            offset += light2

            // Stage 4: REM Sleep
            points.add(HypnogramPoint(offset, cycle, SleepStage.REM, remDuration))
            offset += remDuration

            // Micro-Awakening between cycles
            if (cycle < totalCycles) {
                points.add(HypnogramPoint(offset, cycle, SleepStage.AWAKE, 2))
                offset += 2
            }
        }

        // Final awakening
        points.add(HypnogramPoint(offset, totalCycles, SleepStage.AWAKE, 5))

        return points
    }
}

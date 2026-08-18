package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<SleepLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: SleepLogEntity): Long

    @Delete
    suspend fun deleteLog(log: SleepLogEntity)

    @Query("DELETE FROM sleep_logs WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM sleep_logs")
    suspend fun deleteAllLogs()
}

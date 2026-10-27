package com.shamsicalendar.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY jalaliDate ASC, hour ASC")
    fun getAllEvents(): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE jalaliDate = :date ORDER BY hour ASC")
    fun getEventsForDate(date: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE jalaliDate LIKE :monthPrefix || '%' ORDER BY jalaliDate ASC, hour ASC")
    fun getEventsForMonth(monthPrefix: String): Flow<List<Event>>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getEvent(id: Long): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM events")
    suspend fun getAllOnce(): List<Event>
}

package com.shamsicalendar.app.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class EventRepository(context: Context) {
    private val dao = EventDatabase.get(context).eventDao()

    fun allEvents(): Flow<List<Event>> = dao.getAllEvents()
    fun eventsForDate(jy: Int, jm: Int, jd: Int): Flow<List<Event>> =
        dao.getEventsForDate(Event.formatDate(jy, jm, jd))
    fun eventsForMonth(jy: Int, jm: Int): Flow<List<Event>> =
        dao.getEventsForMonth("$jy-${jm.toString().padStart(2, '0')}")

    suspend fun add(event: Event): Long = dao.insert(event)
    suspend fun update(event: Event) = dao.update(event)
    suspend fun delete(event: Event) = dao.delete(event)
    suspend fun allOnce(): List<Event> = dao.getAllOnce()
}

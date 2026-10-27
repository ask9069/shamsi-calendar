package com.shamsicalendar.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shamsicalendar.app.data.Event
import com.shamsicalendar.app.data.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CalendarViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = EventRepository(app.applicationContext)

    fun eventsForDate(jy: Int, jm: Int, jd: Int): Flow<List<Event>> =
        repo.eventsForDate(jy, jm, jd)

    fun eventsForMonth(jy: Int, jm: Int): Flow<List<Event>> =
        repo.eventsForMonth(jy, jm)

    fun allEvents(): Flow<List<Event>> = repo.allEvents()

    fun addEvent(event: Event, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repo.add(event)
            onComplete(id)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch { repo.delete(event) }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch { repo.update(event) }
    }
}

class CalendarViewModelFactory(private val context: android.content.Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(context.applicationContext as Application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

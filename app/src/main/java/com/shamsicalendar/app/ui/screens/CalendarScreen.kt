package com.shamsicalendar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shamsicalendar.app.calendar.DateConverter
import com.shamsicalendar.app.calendar.JalaliCalendar
import com.shamsicalendar.app.data.Event
import com.shamsicalendar.app.data.Holidays
import com.shamsicalendar.app.ui.components.MonthGrid
import com.shamsicalendar.app.ui.theme.BackgroundCard
import com.shamsicalendar.app.ui.theme.BackgroundElevated
import com.shamsicalendar.app.ui.theme.Border
import com.shamsicalendar.app.ui.theme.GoldLight
import com.shamsicalendar.app.ui.theme.GoldPrimary
import com.shamsicalendar.app.ui.theme.HolidayRed
import com.shamsicalendar.app.ui.theme.TextPrimary
import com.shamsicalendar.app.ui.theme.TextSecondary
import com.shamsicalendar.app.viewmodel.CalendarViewModel
import com.shamsicalendar.app.viewmodel.CalendarViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToEvents: () -> Unit,
    onNavigateToConverter: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModelFactory(context))
    val scope = rememberCoroutineScope()

    val today = remember { JalaliCalendar.today() }
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonth by remember { mutableStateOf(today.month) }
    var selectedDay by remember { mutableStateOf(today.day) }

    val eventsForMonth by viewModel.eventsForMonth(currentYear, currentMonth).collectAsState(initial = emptyList())
    val eventDays = remember(eventsForMonth) {
        eventsForMonth.mapNotNull { e ->
            val (_, _, d) = e.parseJalali()
            d
        }.toSet()
    }

    val selectedDateEvents by viewModel.eventsForDate(currentYear, currentMonth, selectedDay).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${JalaliCalendar.monthNames[currentMonth - 1]} ${JalaliCalendar.toPersianDigits(currentYear)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = JalaliCalendar.season(currentMonth),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        currentYear = today.year
                        currentMonth = today.month
                        selectedDay = today.day
                    }) {
                        Icon(Icons.Default.Today, contentDescription = "امروز", tint = GoldPrimary)
                    }
                    IconButton(onClick = onNavigateToConverter) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "مبدل", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = GoldPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEvents,
                containerColor = GoldPrimary,
                contentColor = Color.Black
            ) {
                Text("＋", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 8.dp)
        ) {
            // نوار ناوبری ماه - در RTL دکمه راست = ماه بعد، دکمه چپ = ماه قبل
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // در RTL، این دکمه سمت راست قرار می‌گیرد = ماه بعد
                IconButton(onClick = {
                    if (currentMonth == 12) {
                        currentMonth = 1; currentYear++
                    } else currentMonth++
                    selectedDay = 1
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "ماه بعد", tint = GoldPrimary)
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = BackgroundElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${JalaliCalendar.monthNames[currentMonth - 1]} ${JalaliCalendar.toPersianDigits(currentYear)}",
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // در RTL، این دکمه سمت چپ قرار می‌گیرد = ماه قبل
                IconButton(onClick = {
                    if (currentMonth == 1) {
                        currentMonth = 12; currentYear--
                    } else currentMonth--
                    selectedDay = 1
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "ماه قبل", tint = GoldPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // شبکه ماه
            MonthGrid(
                jy = currentYear,
                jm = currentMonth,
                selectedDay = selectedDay,
                today = today,
                eventDays = eventDays
            ) { day -> selectedDay = day }

            Spacer(modifier = Modifier.height(12.dp))

            // اطلاعات روز انتخاب‌شده
            SelectedDayCard(
                jy = currentYear,
                jm = currentMonth,
                jd = selectedDay,
                events = selectedDateEvents
            )
        }
    }
}

@Composable
private fun SelectedDayCard(
    jy: Int, jm: Int, jd: Int,
    events: List<Event>
) {
    val tri = remember(jy, jm, jd) { DateConverter.fromJalali(jy, jm, jd) }
    val occasions = remember(jm, jd) { Holidays.getForDay(jm, jd) }
    val dow = remember(jy, jm, jd) { JalaliCalendar.dayOfWeek(jy, jm, jd) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${JalaliCalendar.weekDayNames[dow]} ${JalaliCalendar.toPersianDigits(jd)} ${JalaliCalendar.monthNames[jm - 1]}",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tri.gregorianDisplay,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = tri.islamicDisplay,
                        color = GoldLight,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = JalaliCalendar.toPersianDigits(jd),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }
            }

            if (occasions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Border, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                occasions.forEach { occ ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (occ.isHoliday) HolidayRed else GoldPrimary)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = occ.title,
                            color = if (occ.isHoliday) HolidayRed else TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (occ.isHoliday) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (events.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Border, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "رویدادهای شما",
                    color = GoldPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                events.forEach { ev ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(ev.color))
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = ev.title,
                            color = TextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        if (ev.hour != null) {
                            Text(
                                text = "${JalaliCalendar.toPersianDigits(ev.hour.toString().padStart(2,'0'))}:${JalaliCalendar.toPersianDigits((ev.minute ?: 0).toString().padStart(2,'0'))}",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

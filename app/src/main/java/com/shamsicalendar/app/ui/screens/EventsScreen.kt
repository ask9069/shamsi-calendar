package com.shamsicalendar.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shamsicalendar.app.calendar.JalaliCalendar
import com.shamsicalendar.app.data.Event
import com.shamsicalendar.app.notification.ReminderScheduler
import com.shamsicalendar.app.ui.theme.BackgroundCard
import com.shamsicalendar.app.ui.theme.BackgroundElevated
import com.shamsicalendar.app.ui.theme.Border
import com.shamsicalendar.app.ui.theme.GoldLight
import com.shamsicalendar.app.ui.theme.GoldPrimary
import com.shamsicalendar.app.ui.theme.TextPrimary
import com.shamsicalendar.app.ui.theme.TextSecondary
import com.shamsicalendar.app.viewmodel.CalendarViewModel
import com.shamsicalendar.app.viewmodel.CalendarViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val viewModel: CalendarViewModel = viewModel(factory = CalendarViewModelFactory(context))
    val events by viewModel.allEvents().collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("رویدادهای من", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = GoldPrimary)
                    }
                },
                actions = {
                    TextButton(onClick = { showDialog = true }) {
                        Text("افزودن", color = GoldPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        if (events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(" هنوز رویدادی ثبت نشده است", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("برای افزودن رویداد جدید از دکمه بالا استفاده کنید", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(events, key = { it.id }) { event ->
                    EventCard(
                        event = event,
                        onDelete = {
                            ReminderScheduler.cancel(context, event.id.toInt())
                            viewModel.deleteEvent(event)
                        }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddEventDialog(
            onDismiss = { showDialog = false },
            onConfirm = { ev ->
                viewModel.addEvent(ev) { id ->
                    ReminderScheduler.schedule(context, ev.copy(id = id))
                }
                showDialog = false
            }
        )
    }
}

@Composable
private fun EventCard(event: Event, onDelete: () -> Unit) {
    val (jy, jm, jd) = remember(event.jalaliDate) { event.parseJalali() }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(event.color)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = JalaliCalendar.toPersianDigits(jd),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, color = TextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "${JalaliCalendar.toPersianDigits(jy.toString())}/${JalaliCalendar.toPersianDigits(jm.toString().padStart(2,'0'))}/${JalaliCalendar.toPersianDigits(jd.toString().padStart(2,'0'))}" +
                        (event.hour?.let { " - ${JalaliCalendar.toPersianDigits(it.toString().padStart(2,'0'))}:${JalaliCalendar.toPersianDigits((event.minute ?: 0).toString().padStart(2,'0'))}" } ?: ""),
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
                if (event.description.isNotEmpty()) {
                    Text(event.description, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                }
                if (event.reminderMinutes > 0) {
                    Text(
                        text = "یادآور: ${JalaliCalendar.toPersianDigits(event.reminderMinutes.toString())} دقیقه قبل",
                        color = GoldLight,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(painterResource(com.shamsicalendar.app.R.drawable.ic_delete), contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun AddEventDialog(
    onDismiss: () -> Unit,
    onConfirm: (Event) -> Unit
) {
    val today = remember { JalaliCalendar.today() }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var year by remember { mutableStateOf(today.year.toString()) }
    var month by remember { mutableStateOf(today.month.toString()) }
    var day by remember { mutableStateOf(today.day.toString()) }
    var hour by remember { mutableStateOf("") }
    var minute by remember { mutableStateOf("") }
    var reminder by remember { mutableStateOf("15") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("رویداد جدید", color = GoldPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("توضیحات (اختیاری)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("تاریخ شمسی", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = year, onValueChange = { year = it.filter { c -> c.isDigit() }.take(4) },
                        label = { Text("سال") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                    OutlinedTextField(
                        value = month, onValueChange = { month = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("ماه") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                    OutlinedTextField(
                        value = day, onValueChange = { day = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("روز") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("ساعت (اختیاری)", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = hour, onValueChange = { hour = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("ساعت") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                    OutlinedTextField(
                        value = minute, onValueChange = { minute = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("دقیقه") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                    OutlinedTextField(
                        value = reminder, onValueChange = { reminder = it.filter { c -> c.isDigit() } },
                        label = { Text("یادآور(دقیقه)") }, modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val y = year.toIntOrNull() ?: return@Button
                    val m = month.toIntOrNull() ?: return@Button
                    val d = day.toIntOrNull() ?: return@Button
                    if (title.isBlank()) return@Button
                    val ev = Event(
                        title = title,
                        description = description,
                        jalaliDate = Event.formatDate(y, m, d),
                        hour = hour.toIntOrNull(),
                        minute = minute.toIntOrNull(),
                        reminderMinutes = reminder.toIntOrNull() ?: 0
                    )
                    onConfirm(ev)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) { Text("ذخیره", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("انصراف", color = TextSecondary) }
        }
    )
}

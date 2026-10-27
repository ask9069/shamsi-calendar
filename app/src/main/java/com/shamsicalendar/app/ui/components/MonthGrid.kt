package com.shamsicalendar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shamsicalendar.app.calendar.JalaliCalendar
import com.shamsicalendar.app.data.Holidays
import com.shamsicalendar.app.ui.theme.BackgroundCard
import com.shamsicalendar.app.ui.theme.BackgroundElevated
import com.shamsicalendar.app.ui.theme.Border
import com.shamsicalendar.app.ui.theme.GoldPrimary
import com.shamsicalendar.app.ui.theme.HolidayRed
import com.shamsicalendar.app.ui.theme.TextPrimary
import com.shamsicalendar.app.ui.theme.TextTertiary

@Composable
fun MonthGrid(
    jy: Int,
    jm: Int,
    selectedDay: Int?,
    today: JalaliCalendar.JDate?,
    eventDays: Set<Int>,
    onDayClick: (Int) -> Unit
) {
    val cells = JalaliCalendar.monthGrid(jy, jm)
    val weekDays = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // هدر روزهای هفته
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // شبکه روزها
        cells.chunked(7).forEach { weekRow ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekRow.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (cell) {
                            is JalaliCalendar.GridCell.Empty -> Spacer(Modifier.size(0.dp))
                            is JalaliCalendar.GridCell.Day -> {
                                val isToday = today != null && today.year == jy && today.month == jm && today.day == cell.day
                                val isSelected = selectedDay == cell.day
                                val isHoliday = cell.dayOfWeek == 6 || Holidays.isHoliday(jm, cell.day)
                                val hasEvent = eventDays.contains(cell.day)

                                val bg = when {
                                    isSelected -> GoldPrimary
                                    isToday -> BackgroundElevated
                                    else -> Color.Transparent
                                }
                                val fg = when {
                                    isSelected -> Color.Black
                                    isToday -> GoldPrimary
                                    isHoliday -> HolidayRed
                                    else -> TextPrimary
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(bg)
                                        .then(
                                            if (isToday && !isSelected) Modifier.border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp))
                                            else Modifier
                                        )
                                        .clickable { onDayClick(cell.day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = JalaliCalendar.toPersianDigits(cell.day),
                                            color = fg,
                                            fontSize = 15.sp,
                                            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (hasEvent) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = 2.dp)
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(GoldPrimary)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.shamsicalendar.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.shamsicalendar.app.calendar.DateConverter
import com.shamsicalendar.app.calendar.IslamicCalendar
import com.shamsicalendar.app.calendar.JalaliCalendar
import com.shamsicalendar.app.ui.theme.BackgroundCard
import com.shamsicalendar.app.ui.theme.BackgroundElevated
import com.shamsicalendar.app.ui.theme.GoldLight
import com.shamsicalendar.app.ui.theme.GoldPrimary
import com.shamsicalendar.app.ui.theme.TextPrimary
import com.shamsicalendar.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(onBack: () -> Unit) {
    var jy by remember { mutableStateOf("") }
    var jm by remember { mutableStateOf("") }
    var jd by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<DateConverter.TriCalendar?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مبدل تاریخ", color = GoldPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "تاریخ شمسی را وارد کنید",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = jy,
                    onValueChange = { jy = it.filter { c -> c.isDigit() } },
                    label = { Text("سال") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = jm,
                    onValueChange = { jm = it.filter { c -> c.isDigit() } },
                    label = { Text("ماه") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = jd,
                    onValueChange = { jd = it.filter { c -> c.isDigit() } },
                    label = { Text("روز") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val y = jy.toIntOrNull()
                    val m = jm.toIntOrNull()
                    val d = jd.toIntOrNull()
                    if (y == null || m == null || d == null || m !in 1..12 || d !in 1..31) {
                        error = "لطفاً تاریخ معتبر وارد کنید"
                        result = null
                    } else {
                        error = null
                        result = DateConverter.fromJalali(y, m, d)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = androidx.compose.ui.graphics.Color.Black)
            ) {
                Text("تبدیل کن", fontWeight = FontWeight.Bold)
            }

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            result?.let { tri ->
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("نتیجه تبدیل", color = GoldPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))

                        ConversionRow("شمسی (جلالی)", tri.jalaliDisplay)
                        ConversionRow("میلادی", tri.gregorianDisplay)
                        ConversionRow("قمری (هجری)", tri.islamicDisplay)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // راهنما: تبدیل سریع امروز
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BackgroundElevated),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val todayTri = remember { DateConverter.today() }
                    Text("امروز", color = GoldLight, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(todayTri.jalaliDisplay, color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                    Text(todayTri.gregorianDisplay, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    Text(todayTri.islamicDisplay, color = GoldLight, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ConversionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

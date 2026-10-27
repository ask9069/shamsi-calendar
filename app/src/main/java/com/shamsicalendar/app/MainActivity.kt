package com.shamsicalendar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shamsicalendar.app.ui.screens.CalendarScreen
import com.shamsicalendar.app.ui.screens.ConverterScreen
import com.shamsicalendar.app.ui.screens.EventsScreen
import com.shamsicalendar.app.ui.theme.ShamsiCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShamsiCalendarTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    Scaffold { padding ->
        NavHost(
            navController = navController,
            startDestination = "calendar",
            modifier = Modifier.padding(padding)
        ) {
            composable("calendar") {
                CalendarScreen(
                    onNavigateToEvents = { navController.navigate("events") },
                    onNavigateToConverter = { navController.navigate("converter") }
                )
            }
            composable("events") {
                EventsScreen(onBack = { navController.popBackStack() })
            }
            composable("converter") {
                ConverterScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

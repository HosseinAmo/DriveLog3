package com.example.drivelog3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { DriveLogApp() } }
    }
}

@Composable
fun DriveLogApp() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStart = { nav.navigate("live") },
                onProfile = { nav.navigate("profile") }
            )
        }
        composable("live") { LiveScreen() }
        composable("profile") { ProfileScreen(onDone = { nav.popBackStack() }) }
    }
}

@Composable
fun HomeScreen(onStart: () -> Unit, onProfile: () -> Unit) {
    val ctx = LocalContext.current
    val profile = remember { ProfileStore.load(ctx) }
    val carDetails = listOf(profile.year, profile.make, profile.model)
        .filter { it.isNotBlank() }.joinToString(" ")

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Drive Log", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        if (profile.owner.isNotBlank()) Text("Driver: ${profile.owner}")
        if (profile.carName.isBlank() && carDetails.isBlank()) {
            Text("No car set up yet")
        } else {
            Text("Car: ${profile.carName} $carDetails".trim())
        }
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onProfile) { Text("Edit driver & car profile") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onStart) { Text("Start Session") }
    }
}
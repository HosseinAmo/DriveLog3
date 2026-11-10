package com.example.drivelog3

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

data class CarProfile(
    val owner: String = "",
    val carName: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = ""
)

// Saves and loads the profile in SharedPreferences (a small key-value file on the phone)
object ProfileStore {
    private const val PREFS = "drivelog_prefs"

    fun load(ctx: Context): CarProfile {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return CarProfile(
            owner = p.getString("owner", "") ?: "",
            carName = p.getString("carName", "") ?: "",
            make = p.getString("make", "") ?: "",
            model = p.getString("model", "") ?: "",
            year = p.getString("year", "") ?: ""
        )
    }

    fun save(ctx: Context, c: CarProfile) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("owner", c.owner)
            .putString("carName", c.carName)
            .putString("make", c.make)
            .putString("model", c.model)
            .putString("year", c.year)
            .apply()
    }
}

@Composable
fun ProfileScreen(onDone: () -> Unit) {
    val ctx = LocalContext.current
    val saved = remember { ProfileStore.load(ctx) }
    var owner by remember { mutableStateOf(saved.owner) }
    var carName by remember { mutableStateOf(saved.carName) }
    var make by remember { mutableStateOf(saved.make) }
    var model by remember { mutableStateOf(saved.model) }
    var year by remember { mutableStateOf(saved.year) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Driver & Car Profile", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = owner, onValueChange = { owner = it },
            label = { Text("Your name") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = carName, onValueChange = { carName = it },
            label = { Text("Car nickname") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = make, onValueChange = { make = it },
            label = { Text("Make (e.g. Volkswagen)") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = model, onValueChange = { model = it },
            label = { Text("Model (e.g. Passat)") },
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = year, onValueChange = { year = it },
            label = { Text("Year") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                ProfileStore.save(ctx, CarProfile(owner, carName, make, model, year))
                onDone()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save") }
    }
}
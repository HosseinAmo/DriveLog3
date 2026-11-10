package com.example.drivelog3

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs

data class GForce(val x: Float = 0f, val y: Float = 0f, val peak: Float = 0f)

class LiveViewModel(app: Application) : AndroidViewModel(app), SensorEventListener {
    private val sm = app.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    // Linear acceleration = gravity already removed; fall back to raw accelerometer
    private val sensor: Sensor? = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        ?: sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _g = MutableStateFlow(GForce())
    val g: StateFlow<GForce> = _g

    fun start() {
        sensor?.let { sm.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME) }
    }

    fun stop() = sm.unregisterListener(this)

    override fun onSensorChanged(e: SensorEvent) {
        val x = e.values[0] / SensorManager.GRAVITY_EARTH   // sideways (cornering)
        val y = e.values[1] / SensorManager.GRAVITY_EARTH   // forward/back (accel/braking)
        val peak = maxOf(_g.value.peak, abs(x), abs(y))
        _g.value = GForce(x, y, peak)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onCleared() = stop()
}

@Composable
fun LiveScreen(vm: LiveViewModel = viewModel()) {
    val g by vm.g.collectAsState()

    // start listening when the screen appears, stop when it leaves
    DisposableEffect(Unit) {
        vm.start()
        onDispose { vm.stop() }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Live Recording", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        Text("Lateral G: %.2f".format(g.x), style = MaterialTheme.typography.titleLarge)
        Text("Longitudinal G: %.2f".format(g.y), style = MaterialTheme.typography.titleLarge)
        Text("Peak G: %.2f".format(g.peak), style = MaterialTheme.typography.titleLarge)
    }
}
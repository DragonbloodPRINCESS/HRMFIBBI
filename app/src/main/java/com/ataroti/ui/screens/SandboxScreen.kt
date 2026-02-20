package com.ataroti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ataroti.data.model.ChaosLevel
import com.ataroti.ui.viewmodel.SandboxViewModel

@Composable
fun SandboxScreen(viewModel: SandboxViewModel, onBackLive: () -> Unit) {
    val cards by viewModel.canvas.collectAsState()
    val chaos by viewModel.chaos.collectAsState()

    Column(Modifier.fillMaxSize().background(Color(0xFF141414)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::shuffle) { Text("Shuffle") }
            Button(onClick = viewModel::drawTop) { Text("Draw Top") }
            Button(onClick = viewModel::drawMiddle) { Text("Draw Middle") }
            Button(onClick = viewModel::reshuffle) { Text("Reshuffle") }
            Button(onClick = onBackLive) { Text("Live") }
        }
        Text("Chaos: ${chaos.name}", color = Color(0xFFE8E0CF))
        Slider(value = when (chaos) { ChaosLevel.MINIMAL -> 0f; ChaosLevel.MODERATE -> 0.5f; ChaosLevel.WILD -> 1f },
            onValueChange = {
                viewModel.chaos.value = when {
                    it < 0.34f -> ChaosLevel.MINIMAL
                    it < 0.67f -> ChaosLevel.MODERATE
                    else -> ChaosLevel.WILD
                }
            })
        Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF1B1B1B))) {
            Column(Modifier.align(Alignment.TopStart).padding(8.dp)) {
                cards.takeLast(10).forEach { Text("• ${it.title}", color = Color(0xFFE8E0CF)) }
            }
        }
    }
}

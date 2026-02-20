package com.ataroti.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ataroti.data.model.ChaosLevel
import com.ataroti.ui.viewmodel.SandboxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxScreen(viewModel: SandboxViewModel, onBackLive: () -> Unit) {
    val cards by viewModel.canvas.collectAsState()
    val deck by viewModel.deck.collectAsState()
    val chaos by viewModel.chaos.collectAsState()

    var showDeck by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().background(Color(0xFF141414)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::shuffle) { Text("Shuffle") }
            Button(onClick = viewModel::drawTop) { Text("Draw Top") }
            Button(onClick = viewModel::drawMiddle) { Text("Draw Middle") }
            Button(onClick = { showDeck = true }) { Text("Browse Deck") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = viewModel::reshuffle) { Text("Reshuffle") }
            Button(onClick = onBackLive) { Text("Live") }
        }

        Text("Chaos: ${chaos.name}", color = Color(0xFFE8E0CF))
        Slider(
            value = when (chaos) {
                ChaosLevel.MINIMAL -> 0f
                ChaosLevel.MODERATE -> 0.5f
                ChaosLevel.WILD -> 1f
            },
            onValueChange = {
                viewModel.chaos.value = when {
                    it < 0.34f -> ChaosLevel.MINIMAL
                    it < 0.67f -> ChaosLevel.MODERATE
                    else -> ChaosLevel.WILD
                }
            }
        )

        Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF1B1B1B))) {
            LazyColumn(Modifier.align(Alignment.TopStart).padding(8.dp)) {
                items(cards, key = { it.id }) { card ->
                    Text(
                        text = "• ${card.title} (${card.orientation.name})",
                        color = Color(0xFFE8E0CF),
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.returnCard(card.id) }.padding(vertical = 8.dp)
                    )
                }
            }
        }
        Text("Tap a canvas card to Return Card", color = Color(0xFF9D988E))
    }

    if (showDeck) {
        ModalBottomSheet(onDismissRequest = { showDeck = false }) {
            LazyColumn(Modifier.padding(12.dp)) {
                items(deck, key = { it.cardId }) { card ->
                    Text(
                        card.title,
                        color = Color(0xFFE8E0CF),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.browseDeckPick(card)
                                showDeck = false
                            }
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

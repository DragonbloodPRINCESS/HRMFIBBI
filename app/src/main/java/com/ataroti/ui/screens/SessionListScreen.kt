package com.ataroti.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ataroti.data.model.Mode
import com.ataroti.ui.viewmodel.SessionListViewModel

@Composable
fun SessionListScreen(viewModel: SessionListViewModel, onOpenMode: (Mode) -> Unit, onBack: () -> Unit) {
    val sessions by viewModel.sessions.collectAsState(initial = emptyList())
    Column(Modifier.fillMaxSize().padding(12.dp)) {
        Button(onClick = onBack) { Text("Back") }
        LazyColumn {
            items(sessions) { session ->
                Text(
                    text = "${session.title} • ${session.mode.name}",
                    color = Color(0xFFE8E0CF),
                    modifier = Modifier.fillMaxWidth().clickable { onOpenMode(session.mode) }.padding(12.dp)
                )
            }
        }
    }
}

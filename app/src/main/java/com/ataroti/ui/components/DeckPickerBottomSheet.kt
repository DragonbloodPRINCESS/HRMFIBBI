package com.ataroti.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ataroti.data.db.CardEntity

private val filters = listOf("ALL", "MAJOR", "WANDS", "CUPS", "SWORDS", "PENTACLES")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckPickerBottomSheet(
    cards: List<CardEntity>,
    onQuery: (String) -> Unit,
    onSelect: (CardEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("ALL") }

    val filtered = remember(cards, query, filter) {
        cards.filter {
            val qOk = query.isBlank() || it.title.contains(query, true) || it.kind.contains(query, true)
            val fOk = filter == "ALL" || it.kind == filter
            qOk && fOk
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onQuery(it)
                },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                filters.forEach { value ->
                    AssistChip(
                        onClick = { filter = value },
                        label = { Text(value.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            LazyColumn {
                items(filtered, key = { it.cardId }) { card ->
                    Text(
                        card.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(card) }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

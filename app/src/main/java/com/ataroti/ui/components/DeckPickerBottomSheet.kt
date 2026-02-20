package com.ataroti.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckPickerBottomSheet(cards: List<CardEntity>, onQuery: (String) -> Unit, onSelect: (CardEntity) -> Unit, onDismiss: () -> Unit) {
    var query by remember { mutableStateOf("") }
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(12.dp)) {
            OutlinedTextField(value = query, onValueChange = { query = it; onQuery(it) }, label = { Text("Search / filter") }, modifier = Modifier.fillMaxWidth())
            LazyColumn {
                items(cards) { card ->
                    Text(card.title, modifier = Modifier.fillMaxWidth().clickable { onSelect(card) }.padding(12.dp))
                }
            }
        }
    }
}

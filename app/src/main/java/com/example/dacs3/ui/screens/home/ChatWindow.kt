package com.example.dacs3.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dacs3.models.Message

val dummyMessages = listOf(
    Message("Alice", "Hello team!", false),
    Message("You", "Hi Alice!", true),
    Message("Bob", "Good morning.", false)
)

@Composable
fun ChatWindow(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dummyMessages.size) { idx ->
            val msg = dummyMessages[idx]
            Row(
                horizontalArrangement = if (msg.isMine) Arrangement.End else Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .padding(horizontal = if (msg.isMine) 40.dp else 0.dp)
                        .padding(horizontal = if (msg.isMine) 0.dp else 40.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        if (!msg.isMine) {
                            Text(msg.author, style = MaterialTheme.typography.labelMedium)
                        }
                        Text(msg.content, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

package com.example.dacs3.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class Message(
    val author: String,
    val content: String,
    val timestamp: String
)

@Composable
fun MessageItem(message: Message) {
    Card(modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = message.author, style = MaterialTheme.typography.titleSmall)
            Text(text = message.content, style = MaterialTheme.typography.bodyMedium)
            Text(text = message.timestamp, style = MaterialTheme.typography.bodySmall)
        }
    }
}

package com.example.dacs3.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.dacs3.R

@Composable
fun MessageInputBar(modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Type a message") },
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { /* attach file */ }) {
            Icon(painterResource(R.drawable.baseline_attach_file_24), contentDescription = "Attach")
        }
        IconButton(onClick = { /* send message(text) */ }) {
            Icon(painterResource(R.drawable.ic_send), contentDescription = "Send")
        }
    }
}

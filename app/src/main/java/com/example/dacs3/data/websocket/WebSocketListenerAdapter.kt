package com.example.dacs3.data.websocket

import com.example.dacs3.data.model.Message
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Inject

class WebSocketListenerAdapter @Inject constructor(
    private val gson: Gson,
    private val onMessageReceived: (Message) -> Unit
) : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("WebSocket connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        try {
            val message = gson.fromJson(text, Message::class.java)
            onMessageReceived(message)
        } catch (e: JsonSyntaxException) {
            println("Error parsing message: ${e.message}")
        }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        println("Received bytes: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket closing: $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("WebSocket error: ${t.message}")
    }
}
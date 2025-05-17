package com.example.dacs3.data.websocket

import com.example.dacs3.data.local.dao.MessageDao
import com.example.dacs3.data.local.entity.MessageEntity
import com.example.dacs3.data.model.Message
import com.example.dacs3.di.IoDispatcher
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val messageDao: MessageDao,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(ioDispatcher)
    private var isConnected = false

    fun start(url: String) {
        if (!isConnected) {
            val listener = WebSocketListenerAdapter(gson) { message ->
                scope.launch {
                    val entity = MessageEntity.fromMessage(message)
                    messageDao.insertMessage(entity)
                }
            }
            webSocketClient.connect(url, listener)
            isConnected = true
        }
    }

    suspend fun sendMessage(message: Message) {
        val json = gson.toJson(message)
        webSocketClient.send(json)
    }

    fun stop() {
        if (isConnected) {
            webSocketClient.close()
            isConnected = false
        }
    }

    fun isConnected(): Boolean = isConnected
}
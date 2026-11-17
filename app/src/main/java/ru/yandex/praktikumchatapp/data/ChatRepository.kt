package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlin.math.pow
import kotlinx.coroutines.flow.retryWhen
import kotlin.time.Duration.Companion.milliseconds

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    companion object {
        private const val BASE_RETRY_DELAY = 100L
    }

    fun getReplyMessage(): Flow<String> {
        return api.getReply()
            .retryWhen { _, attempt ->
                delay((BASE_RETRY_DELAY * (2f.pow(attempt.toFloat())).toLong()).milliseconds)
                true
            }// TODO Задание 2: добавьте обработку ошибок
    }
}
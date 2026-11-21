package ru.yandex.praktikumchatapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.math.pow
import kotlinx.coroutines.flow.retryWhen
import ru.yandex.praktikumchatapp.presentation.Message
import kotlin.time.Duration.Companion.milliseconds

class ChatRepository(
    private val api: ChatApi = ChatApi()
) {

    companion object {
        private const val BASE_RETRY_DELAY = 100L
    }

    fun getReplyMessage(): Flow<Message> {
        return api.getReply()
            .retryWhen { _, attempt ->
                delay((BASE_RETRY_DELAY * (2f.pow(attempt.toFloat())).toLong()).milliseconds)
                true
            }.map(Message::OtherMessage)// TODO Задание 2: добавьте обработку ошибок
    }
}
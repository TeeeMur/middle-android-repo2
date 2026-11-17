package ru.yandex.praktikumchatapp.presentation

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.yandex.praktikumchatapp.data.ChatRepository

class ChatViewModel(
    val isWithReplies: Boolean = true
) : ViewModel() {

    private val repository = ChatRepository()

    //private val _messages = MutableLiveData<List<Message>>(emptyList())  // TODO Задание 1: замените на Flow

    private val _messagesFlow = MutableStateFlow<List<Message>>(emptyList())

    // TODO Задание 3: добавьте состояние shouldShowKeyboard
    private val _shouldShowKeyboard = MutableStateFlow(false)

    val chatState = combine(_shouldShowKeyboard, _messagesFlow) {
        showKeyboard, messages -> ChatState(showKeyboard, messages)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChatState(false, emptyList())
    )



    // TODO Задание 4: замените messages и shouldShowKeyboard на state

    init {
        viewModelScope.launch {
            while (isWithReplies) {
                repository.getReplyMessage().collectIndexed { idx, response ->
                    if (idx == 0) _shouldShowKeyboard.value = true
                    _messagesFlow.update { it + Message.MyMessage(response) }
                }
            }
        }
    }

    fun sendMyMessage(messageText: String) {
        _messagesFlow.update { it + Message.MyMessage(messageText) }
    }
}

// List - нестабильный для compose интерфейс
@Immutable
data class ChatState(
    val shouldShowKeyboard: Boolean,
    val messages: List<Message>
)
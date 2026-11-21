package ru.yandex.praktikumchatapp

import app.cash.turbine.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.yandex.praktikumchatapp.presentation.ChatViewModel
import ru.yandex.praktikumchatapp.presentation.Message

@ExperimentalCoroutinesApi
class ChatViewModelTest {

    private var testDispatcher: TestDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatViewModel(isWithReplies = false)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `send message should update state with MyMessage`() = runTest {
        val message = Message.MyMessage("TestMessage")
        // TODO Задание 5: допишите юнит-тест
        viewModel.sendMyMessage(message.text)
        viewModel.chatState.test {
            awaitItem()
            val item = awaitItem()
            println(item)
            assert(item.messages.size == 1)
            assert(item.messages.last() == message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testReceiveMessage_concurrentMessages() = runTest {
        val messagesToSend = (1..100).map { Message.MyMessage("Message $it") }
        viewModel = ChatViewModel(false)
        val scope = CoroutineScope(testDispatcher)
        messagesToSend.forEach { message ->
            scope.launch { viewModel.sendMyMessage(message.text) }
        }
        advanceUntilIdle()
        viewModel.chatState.test {
            awaitItem()
            val chatState = awaitItem()
            assert(chatState.messages == messagesToSend)
            cancelAndIgnoreRemainingEvents()
        }
        // TODO Задание 6: допишите юнит-тест
    }
}
@file:Suppress("IllegalIdentifier")

package org.kotlinacademy

import org.kotlinacademy.common.Cancellable
import org.kotlinacademy.data.Feedback
import org.kotlinacademy.data.FirebaseTokenType
import org.kotlinacademy.data.NewsData
import org.kotlinacademy.presentation.feedback.FeedbackPresenter
import org.kotlinacademy.presentation.feedback.FeedbackView
import org.kotlinacademy.presentation.news.NewsPresenter
import org.kotlinacademy.presentation.news.NewsView
import org.kotlinacademy.presentation.notifications.RegisterNotificationTokenPresenter
import org.kotlinacademy.presentation.notifications.RegisterNotificationTokenView
import org.kotlinacademy.respositories.FeedbackRepository
import org.kotlinacademy.respositories.NotificationRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RegisterNotificationTokenPresenterUnitTest {

    @BeforeTest
    fun setUp() {
        overrideNotificationRepository { _, _ -> }
    }

    @Test
    fun `When repository returns error, it is shown on view`() {
        val view = RegisterNotificationTokenView()
        overrideNotificationRepository { _, _ -> throw NORMAL_ERROR }
        val presenter = RegisterNotificationTokenPresenter(view, FAKE_TOKEN_TYPE)
        // When
        presenter.onRefresh(FAKE_TOKEN)
        // Then
        assertEquals(1, view.loggedErrors.size)
        assertEquals(NORMAL_ERROR, view.loggedErrors[0])
    }

    @Test
    fun `No error is shown when data sent correctly`() {
        val view = RegisterNotificationTokenView()
        overrideNotificationRepository { _, _ -> /* no-op */ }
        val presenter = RegisterNotificationTokenPresenter(view, FAKE_TOKEN_TYPE)
        // When
        presenter.onRefresh(FAKE_TOKEN)
        // Then
        assertEquals(0, view.loggedErrors.size)
    }

    private fun overrideNotificationRepository(onAddFeedback: (String, FirebaseTokenType) -> Unit) {
        NotificationRepository.override = object : NotificationRepository {
            override suspend fun registerToken(token: String, type: FirebaseTokenType) {
                onAddFeedback(token, type)
            }
        }
    }

    private fun RegisterNotificationTokenView() = object : RegisterNotificationTokenView {
        var loggedErrors = listOf<Throwable>()

        override fun logError(error: Throwable) {
            loggedErrors += error
        }
    }

    companion object {
        val FAKE_TOKEN = "Token"
        val FAKE_TOKEN_TYPE = FirebaseTokenType.Android
        val NORMAL_ERROR = Error()
    }
}
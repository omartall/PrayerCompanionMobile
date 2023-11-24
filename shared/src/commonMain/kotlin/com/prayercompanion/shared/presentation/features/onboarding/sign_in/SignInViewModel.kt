package com.prayercompanion.shared.presentation.features.onboarding.sign_in

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.prayercompanion.shared.domain.repositories.AuthenticationRepository
import com.prayercompanion.shared.domain.usecases.AccountSignIn
import com.prayercompanion.shared.domain.utils.Task
import com.prayercompanion.shared.domain.utils.tracking.TrackedButtons
import com.prayercompanion.shared.domain.utils.tracking.Tracker
import com.prayercompanion.shared.presentation.navigation.Route
import com.prayercompanion.shared.presentation.utils.StringRes
import com.prayercompanion.shared.presentation.utils.StringResourceReader
import com.prayercompanion.shared.presentation.utils.UiEvent
import com.prayercompanion.shared.presentation.utils.printStackTraceInDebug
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val accountSignIn: AccountSignIn,
    private val tracker: Tracker,
    private val stringResourceReader: StringResourceReader
) : KMMViewModel() {

    private val _uiEventsChannel = Channel<UiEvent>()
    val uiEventsChannel = _uiEventsChannel.receiveAsFlow()
    var isLoading by mutableStateOf(false)
        private set

    fun onEvent(event: SignInEvents) {
        when (event) {
            is SignInEvents.OnSignInWithGoogleResultReceived -> onSignInWithGoogleResultReceived(
                event.result,
                event.task
            )

            is SignInEvents.OnSignInWithGoogleClicked -> onSignInWithGoogleClicked()

            is SignInEvents.OnSignInAnonymously -> onSignInAnonymouslyClicked()
        }
    }

    private fun onSignInWithGoogleResultReceived(result: Boolean, task: Task<String>) {
        if (result.not()) {
            sendErrorEvent(stringResourceReader.read(StringRes.error_something_went_wrong))
            return
        }

        isLoading = true
        if (task.isSuccessful) {
            val token = task.result ?: return
            viewModelScope.coroutineScope.launch {

                authenticationRepository.signInWithGoogle(
                    token,
                    onSuccess = ::onSignInSuccess,
                    onFailure = ::onSignInFail,
                )
            }
        } else {
            isLoading = false
            sendErrorEvent(task.exception?.message)
            task.exception?.printStackTraceInDebug()
        }
    }

    private fun onSignInWithGoogleClicked() {
        tracker.trackButtonClicked(TrackedButtons.GOOGLE_SIGN_IN)
        sendEvent(UiEvent.LaunchSignInWithGoogle)
    }

    private fun onSignInAnonymouslyClicked() {
        authenticationRepository.signInAnonymously(
            onSuccess = ::onSignInSuccess,
            onFailure = ::onSignInFail
        )
    }

    private fun onSignInSuccess() {
        isLoading = false
        tracker.trackLogin()
        viewModelScope.coroutineScope.launch(Dispatchers.IO) {
            val signInResult = accountSignIn.call()
            signInResult.onSuccess {
                sendEvent(UiEvent.Navigate(Route.PermissionsRequests))
            }.onFailure {
                sendErrorEvent(it.message)
            }
        }
    }

    private fun onSignInFail(exception: Exception) {
        isLoading = false
        sendErrorEvent(exception.message)
    }

    private fun sendErrorEvent(text: String?) {
        if (text.isNullOrBlank()) return
        sendEvent(UiEvent.ShowErrorSnackBarStr(text))
    }

    private fun sendEvent(uiEvent: UiEvent) {
        viewModelScope.coroutineScope.launch {
            _uiEventsChannel.send(uiEvent)
        }
    }

}
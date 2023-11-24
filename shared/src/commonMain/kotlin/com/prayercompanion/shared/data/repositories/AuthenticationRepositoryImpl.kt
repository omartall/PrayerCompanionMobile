package com.prayercompanion.shared.data.repositories

import com.prayercompanion.shared.data.preferences.DataStoresRepo
import com.prayercompanion.shared.domain.repositories.AuthenticationRepository
import com.prayercompanion.shared.presentation.utils.printStackTraceInDebug
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//expect class AuthenticationRepositoryImplX : AuthenticationRepository {
//
//    override suspend fun isSignedIn(): Boolean
//
//    override suspend fun signInWithGoogle(
//        token: String,
//        onSuccess: () -> Unit,
//        onFailure: (Exception) -> Unit
//    )
//
//    override fun signInAnonymously(
//        onSuccess: () -> Unit,
//        onFailure: (Exception) -> Unit
//    )
//}

class AuthenticationRepositoryImpl constructor(
    private val dataStoresRepo: DataStoresRepo,
): AuthenticationRepository {

    private val auth = Firebase.auth

    override suspend fun isSignedIn(): Boolean {
        val user = auth.currentUser

        // if the user object is not null means they are signed in
        dataStoresRepo.updateAppPreferencesDataStore {
            it.copy(isSignedIn = user != null)
        }
        return user != null
    }

    override suspend fun signInWithGoogle(
        token: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.credential(token, null)
        try {
            auth.signInWithCredential(credential)

            CoroutineScope(Dispatchers.Default).launch {
                dataStoresRepo.updateAppPreferencesDataStore {
                    it.copy(isSignedIn = true)
                }
            }
            onSuccess()
        } catch (e: Exception) {
            onSignInFail(e, onFailure)
            e.printStackTraceInDebug()
        }
    }

    override fun signInAnonymously(onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        TODO("Not yet implemented")
    }

    private suspend fun onSignInFail(
        exception: Exception,
        onFailure: (Exception) -> Unit
    ) {
        signOut()
        onFailure(exception)
        exception.printStackTraceInDebug()
    }

    private suspend fun signOut() {
        auth.signOut()
    }
}
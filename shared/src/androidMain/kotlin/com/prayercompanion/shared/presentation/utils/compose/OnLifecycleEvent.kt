package com.prayercompanion.shared.presentation.utils.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
actual fun OnLifecycleEvent(onEvent: (event: LifecycleEvent) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            val lifecycleEvent = when (event) {
                Lifecycle.Event.ON_START -> {
                    LifecycleEvent.ON_START
                }

                Lifecycle.Event.ON_PAUSE -> {
                    LifecycleEvent.ON_PAUSE
                }

                else -> {
                    return@LifecycleEventObserver
                }
            }
            eventHandler.value(lifecycleEvent)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
package com.mj.app.bargainprice.ui.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember


sealed interface Event {
    data class OpenLink(val url: String) : Event
    data object Authenticate : Event
    data object SkipLogin : Event
}

@Stable
class HoistingEventController(
    val openLink: (String) -> Unit,
    val authenticate: () -> Unit,
    val skipLogin: () -> Unit,
)

@Composable
fun rememberHoistingEventController(
    callback: HoistingEventCallback
): HoistingEventController {
    return remember {
        HoistingEventController(
            openLink = { link -> callback.onEventReceived(Event.OpenLink(link)) },
            authenticate = { callback.onEventReceived(Event.Authenticate) },
            skipLogin = { callback.onEventReceived(Event.SkipLogin) },
        )
    }
}
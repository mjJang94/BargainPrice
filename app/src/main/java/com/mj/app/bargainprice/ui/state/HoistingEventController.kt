package com.mj.app.bargainprice.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember


sealed interface Event {
    data class OpenLink(val url: String) : Event
}

@Stable
class HoistingEventController(
    val openLink: (String) -> Unit
)

@Composable
fun rememberHoistingEventController(
    callback: HoistingEventCallback
): HoistingEventController {
    return remember {
        HoistingEventController(
            openLink = { link -> callback.onEventReceived(Event.OpenLink(link)) }
        )
    }
}
package com.matthewbennin.hatvdash.ui

import android.view.KeyEvent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import com.matthewbennin.hatvdash.logic.RemotePressHandler

/**
 * Encapsulates focus handling and remote key press dispatch to [RemotePressHandler].
 * Use [rememberRemoteKeyInteractions] to obtain an instance and apply [modifier]
 * to the composable that should handle DPAD center/enter events.
 */
class RemoteKeyInteractions(
    val modifier: Modifier,
    val isFocused: State<Boolean>,
    val focusRequester: FocusRequester
)

@Composable
fun rememberRemoteKeyInteractions(
    onSingleTap: () -> Unit,
    onDoubleTap: () -> Unit,
    onLongPress: () -> Unit = {},
    onPostLongPressRelease: (() -> Unit)? = null
): RemoteKeyInteractions {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused = interactionSource.collectIsFocusedAsState()
    val focusRequester = remember { FocusRequester() }

    val modifier = Modifier
        .focusRequester(focusRequester)
        .focusable(interactionSource = interactionSource)
        .onKeyEvent { event ->
            val key = event.nativeKeyEvent
            if (key.keyCode == KeyEvent.KEYCODE_DPAD_CENTER ||
                key.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                RemotePressHandler.handleKeyEvent(
                    event = key,
                    onSingleTap = onSingleTap,
                    onDoubleTap = onDoubleTap,
                    onLongPress = onLongPress,
                    onPostLongPressRelease = onPostLongPressRelease
                )
                true
            } else {
                false
            }
        }

    return RemoteKeyInteractions(modifier, isFocused, focusRequester)
}
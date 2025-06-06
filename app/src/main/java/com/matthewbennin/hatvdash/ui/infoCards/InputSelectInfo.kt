package com.matthewbennin.hatvdash.ui.infoCards

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matthewbennin.hatvdash.data.EntityStateManager
import kotlinx.coroutines.delay
import org.json.JSONObject
import org.json.JSONArray
import android.view.KeyEvent.KEYCODE_DPAD_CENTER
import androidx.compose.foundation.focusGroup

@Composable
fun InputSelectInfo(entityId: String, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val state = EntityStateManager.getState(entityId)
    val attributes = state?.optJSONObject("attributes") ?: JSONObject()
    val options = attributes.optJSONArray("options") ?: JSONArray()
    val currentValue = state?.optString("state") ?: "..."

    val firstButtonFocusRequester = remember { FocusRequester() }
    val focusedIndex = remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(100)
        firstButtonFocusRequester.requestFocus()
    }

    // Top-level key handling box
    Box(
        modifier = Modifier
            .onKeyEvent { event ->
                val nativeCode = event.nativeKeyEvent.keyCode
                Log.d("KeyTest", "nativeKeyCode=$nativeCode type=${event.type}")

                if (event.type == KeyEventType.KeyUp &&
                    nativeCode == KEYCODE_DPAD_CENTER
                ) {
                    val selectedOption = options.optString(focusedIndex.intValue)
                    Log.d("InputSelectInfo", "Selected $selectedOption")
                    if (selectedOption != currentValue) {
//                        HaWebSocketManager.executeCommand(
//                            service = "select_option",
//                            domain = "input_select",
//                            entityId = entityId,
//                            data = JSONObject().put("option", selectedOption)
//                        )
                    }
                    onDismiss()
                    true
                } else {
                    false
                }
            }
            .focusable()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(32.dp)
                .focusable(),
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = attributes.optString("friendly_name", entityId),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Current: $currentValue",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusGroup()
                ) {
                    itemsIndexed((0 until options.length()).map { options.optString(it) }) { index, option ->
                        val isSelected = option == currentValue
                        val isFocused = focusedIndex.intValue == index
                        val buttonFocus = remember { FocusRequester() }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isFocused -> Color(0xFF2196F3)
                                        isSelected -> Color(0xFF3F51B5)
                                        else -> Color.Transparent
                                    }
                                )
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        focusedIndex.intValue = index
                                    }
                                }
                                .focusRequester(if (index == 0) firstButtonFocusRequester else buttonFocus)
                                .focusable()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(option, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}

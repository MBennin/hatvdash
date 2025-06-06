package com.matthewbennin.hatvdash.logic

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent

object RemotePressHandler {

    private const val TAG = "RemotePressHandler"

    private const val DOUBLE_TAP_TIMEOUT = 300L
    private const val LONG_PRESS_TIMEOUT = 500L

    private val handler = Handler(Looper.getMainLooper())

    private var lastTapTime = 0L
    private var pressStartTime = 0L
    private var isHandlingPress = false
    private var longPressFired = false

    private var pendingSingleTap: Runnable? = null
    private var longPressRunnable: Runnable? = null

    fun handleKeyEvent(
        event: KeyEvent,
        onSingleTap: () -> Unit,
        onDoubleTap: () -> Unit,
        onLongPress: () -> Unit
    ): Boolean {
        val now = System.currentTimeMillis()

        when (event.action) {
            KeyEvent.ACTION_DOWN -> {
                if (isHandlingPress) {
                    // Prevent repeated DOWN events during hold
                    return true
                }

                isHandlingPress = true
                longPressFired = false
                pressStartTime = now

                // Schedule long press
                longPressRunnable = Runnable {
                    if (isHandlingPress) {
                        longPressFired = true
                        Log.d(TAG, "Long press triggered")
                        cancelPendingSingleTap()
                        onLongPress()
                    }
                }
                handler.postDelayed(longPressRunnable!!, LONG_PRESS_TIMEOUT)
            }

            KeyEvent.ACTION_UP -> {
                if (!isHandlingPress) {
                    return true // Already handled
                }

                isHandlingPress = false

                longPressRunnable?.let { handler.removeCallbacks(it) }
                longPressRunnable = null

                val pressDuration = now - pressStartTime

                if (longPressFired) {
                    Log.d(TAG, "Skipping ACTION_UP — long press already fired")
                    return true
                }

                if (now - lastTapTime <= DOUBLE_TAP_TIMEOUT) {
                    cancelPendingSingleTap()
                    Log.d(TAG, "Double tap triggered")
                    onDoubleTap()
                    lastTapTime = 0L
                } else {
                    pendingSingleTap = Runnable {
                        Log.d(TAG, "Single tap triggered")
                        onSingleTap()
                    }
                    handler.postDelayed(pendingSingleTap!!, DOUBLE_TAP_TIMEOUT)
                    lastTapTime = now
                }
            }
        }

        return true
    }

    private fun cancelPendingSingleTap() {
        pendingSingleTap?.let {
            handler.removeCallbacks(it)
            Log.d(TAG, "Cancelled pending single tap")
        }
        pendingSingleTap = null
    }

    fun cancelAll() {
        cancelPendingSingleTap()
        longPressRunnable?.let { handler.removeCallbacks(it) }
        longPressRunnable = null
        isHandlingPress = false
        longPressFired = false
        Log.d(TAG, "All pending actions cancelled")
    }
}

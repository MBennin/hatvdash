package com.matthewbennin.hatvdash.ui

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import com.matthewbennin.hatvdash.MdiIconManager

/**
 * Helper to load and cache an MDI icon bitmap for a given icon name and tint color.
 * The bitmap is remembered and updated whenever [mdiIcon] or [tintColor] changes.
 */
@Composable
fun rememberMdiIconBitmap(
    mdiIcon: String,
    tintColor: Int
): State<Bitmap?> {
    val context = LocalContext.current
    return produceState<Bitmap?>(initialValue = null, mdiIcon, tintColor) {
        MdiIconManager.loadOrFetchIcon(context, mdiIcon, tintColor) { bitmap ->
            value = bitmap
        }
    }
}
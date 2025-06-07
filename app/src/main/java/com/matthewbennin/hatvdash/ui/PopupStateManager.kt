package com.matthewbennin.hatvdash.ui

import androidx.compose.runtime.mutableStateOf

object PopupStateManager {
    val moreInfoEntityId = mutableStateOf<String?>(null)

    fun show(entityId: String) {
        moreInfoEntityId.value = entityId
    }

    fun dismiss() {
        moreInfoEntityId.value = null
    }

    fun isOpenFor(entityId: String): Boolean {
        return moreInfoEntityId.value == entityId
    }
}

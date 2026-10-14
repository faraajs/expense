package com.example.autospending.ui.components

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun HapticFeedback.performLight() {
    try {
        performHapticFeedback(HapticFeedbackType.LongPress)
    } catch (_: Throwable) {
        // OEM may no-op
    }
}

fun HapticFeedback.performConfirm() {
    try {
        performHapticFeedback(HapticFeedbackType.LongPress)
    } catch (_: Throwable) {
        // OEM may no-op
    }
}

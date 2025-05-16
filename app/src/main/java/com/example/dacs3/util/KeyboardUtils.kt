package com.example.dacs3.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.SoftwareKeyboardController
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Extension function to dismiss keyboard when tapping outside of text fields
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.addFocusCleaner(
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        )
    }
}

/**
 * Extension function for better interaction feedback while dismissing keyboard
 */
suspend fun MutableInteractionSource.emitPress() = coroutineScope {
    launch {
        val pressPosition = Offset.Zero
        emit(PressInteraction.Press(pressPosition))
        emit(PressInteraction.Release(PressInteraction.Press(pressPosition)))
    }
} 
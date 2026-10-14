package com.example.autospending.ui

sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
}

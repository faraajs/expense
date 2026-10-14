package com.example.autospending.ui

enum class ChatRole {
    User,
    Assistant
}

data class ChatMessage(
    val role: ChatRole,
    val text: String
)

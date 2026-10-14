package com.example.autospending.parser

data class NotificationPayload(
    val packageName: String,
    val title: String,
    val text: String,
    val postedAt: Long
)

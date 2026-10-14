package com.example.autospending.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.autospending.ui.ChatMessage
import com.example.autospending.ui.ChatRole

@Composable
fun TanyaAiScreen(
    chatMessages: List<ChatMessage>,
    isAiLoading: Boolean,
    apiKey: String,
    onSendMessage: (String) -> Unit,
    onSaveApiKey: (String) -> Unit,
    onClearChat: () -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(apiKey) }
    LaunchedEffect(apiKey) {
        if (apiKey.isNotBlank() && apiKeyInput != apiKey) {
            apiKeyInput = apiKey
        }
    }
    var userInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.lastIndex)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Gemini API Key (Google AI Studio)", fontWeight = FontWeight.SemiBold)
                Text(
                    "Jawaban AI butuh internet; ringkasan transaksi bulan ini dikirim ke Google Gemini.",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Gemini API Key") },
                        placeholder = { Text("AIza...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation()
                    )
                    Button(
                        onClick = { onSaveApiKey(apiKeyInput.trim()) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🤖", style = MaterialTheme.typography.headlineLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Halo! Tanya apa saja tentang keuangan lo bulan ini.",
                                fontWeight = FontWeight.Medium,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            } else {
                items(
                    items = chatMessages,
                    key = { msg -> "${msg.role.name}:${msg.text.length}:${System.identityHashCode(msg)}" }
                ) { message ->
                    val isUser = message.role == ChatRole.User
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) "Lo" else "Asisten AI",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isUser) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.secondary
                                }
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(text = message.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }

        if (isAiLoading) {
            Text("AI sedang berpikir & menghitung...", style = MaterialTheme.typography.bodySmall)
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                placeholder = { Text("Tanya asisten keuangan...") },
                modifier = Modifier.weight(1f),
                enabled = !isAiLoading
            )
            Button(
                onClick = {
                    onSendMessage(userInput)
                    userInput = ""
                },
                enabled = userInput.isNotBlank() && !isAiLoading,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text("Kirim")
            }
        }

        if (chatMessages.isNotEmpty()) {
            OutlinedButton(
                onClick = onClearChat,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAiLoading
            ) {
                Text("Hapus Riwayat Chat")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

package com.example.autospending.ai

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Gemini generateContent client — network only, no UI/ViewModel state.
 */
class GeminiClient {
    suspend fun generate(apiKey: String, prompt: String): String = withContext(Dispatchers.IO) {
        // Stable models; Gemini 1.5/2.0 endpoints have been shut down.
        val models = listOf(
            "gemini-2.5-flash",
            "gemini-2.5-flash-lite",
        )
        val errors = mutableListOf<String>()
        for (model in models) {
            val result = callModel(apiKey, model, prompt)
            if (result.isSuccess) {
                val text = result.getOrThrow()
                return@withContext if (model == models.first()) text
                else "$text\n\n_(model: $model)_"
            }
            errors += "$model: ${result.exceptionOrNull()?.message}"
        }
        "Gagal memanggil Gemini. Cek API key AI Studio / kuota / koneksi.\n" +
            errors.joinToString("\n") { it.take(280) }
    }

    private fun callModel(apiKey: String, model: String, prompt: String): Result<String> {
        return try {
            val url = URL(
                "https://generativelanguage.googleapis.com/v1beta/models/" +
                    "$model:generateContent?key=$apiKey"
            )
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 30_000
            conn.readTimeout = 90_000
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.doOutput = true

            val requestBody = JSONObject().apply {
                put(
                    "contents",
                    JSONArray().put(
                        JSONObject().put(
                            "parts",
                            JSONArray().put(JSONObject().put("text", prompt))
                        )
                    )
                )
            }

            conn.outputStream.use { os ->
                OutputStreamWriter(os, Charsets.UTF_8).use { osw ->
                    osw.write(requestBody.toString())
                    osw.flush()
                }
            }

            val responseCode = conn.responseCode
            val bodyStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = bodyStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (responseCode !in 200..299) {
                return Result.failure(IllegalStateException("HTTP $responseCode ${responseText.take(400)}"))
            }

            val json = JSONObject(responseText)
            val candidates = json.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return Result.failure(IllegalStateException("empty candidates ${responseText.take(200)}"))
            }
            val content = candidates.getJSONObject(0).optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text").orEmpty()
            if (text.isBlank()) {
                Result.failure(IllegalStateException("empty text"))
            } else {
                Result.success(text)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

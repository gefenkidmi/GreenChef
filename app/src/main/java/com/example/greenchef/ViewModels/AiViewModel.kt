package com.example.greenchef.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.Objects.OpenAiRequestBody
import com.example.greenchef.Objects.Content
import com.example.greenchef.Objects.Part
import com.example.greenchef.Objects.RecipeResponse
import com.example.greenchef.Models.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AiViewModel : ViewModel() {

    private val _responseText = MutableLiveData<String>()
    val responseText: LiveData<String> get() = _responseText

    private val openAiService = RetrofitInstance.geminiApi

    fun generateContent(prompt: String, apiKey: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val requestBody = OpenAiRequestBody(
                    contents = listOf(Content(parts = listOf(Part(prompt))))
                )

                val response = withContext(Dispatchers.IO) {
                    openAiService.generateContent(requestBody, apiKey)
                }

                if (response.isSuccessful && response.body() != null) {
                    val candidates = response.body()?.candidates
                    val resultText = candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()

                    if (!resultText.isNullOrEmpty()) {
                        _responseText.value = resultText ?: "No response received"
                    } else {
                        _responseText.value = "Empty response"
                    }
                } else {
                    _responseText.value = "Error: ${response.code()} - ${response.message()}"
                    Log.e("GeminiAPI", "API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                _responseText.value = "Exception: ${e.message}"
                Log.e("GeminiAPI", "Exception: ${e.message}")
            }
        }
    }
}
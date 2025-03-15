package com.example.greenchef

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.greenchef.Models.OpenAiRequestBody
import com.example.greenchef.Models.RetrofitInstance
import com.example.greenchef.Models.RecipeResponse
import com.example.greenchef.Services.OpenAiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AiViewModel : ViewModel() {

    // Use RetrofitInstance to get an instance of OpenAiService
    private val openAiService = RetrofitInstance.api

    // LiveData to hold the recipe suggestion response
    private val _recipeSuggestion = MutableLiveData<String>()
    val recipeSuggestion: LiveData<String> get() = _recipeSuggestion

    // LiveData for loading state to show a loading spinner if needed
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    // Function to fetch the recipe suggestion from the API
    fun fetchRecipeSuggestion(prompt: String) {
        _loading.value = true  // Set loading to true before making the API call
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Call the OpenAi API using Retrofit instance
                val response = withContext(Dispatchers.IO) {
                    openAiService.getRecipeSuggestions(
                        OpenAiRequestBody(prompt = prompt)
                    )
                }

                // Handle the response
                if (response.isSuccessful) {
                    val suggestion = response.body()?.choices?.get(0)?.text ?: "No suggestion found"
                    _recipeSuggestion.postValue(suggestion)  // Update LiveData with suggestion
                } else {
                    _recipeSuggestion.postValue("Failed to get suggestion")
                }
            } catch (e: Exception) {
                _recipeSuggestion.postValue("Error: ${e.message}")
            } finally {
                _loading.value = false  // Set loading to false after the API call completes
            }
        }
    }
}

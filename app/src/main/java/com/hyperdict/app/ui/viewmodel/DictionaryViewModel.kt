package com.hyperdict.app.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyperdict.app.data.local.DatabaseDownloader
import com.hyperdict.app.data.local.DownloadProgress
import com.hyperdict.app.data.local.WordSuggestion
import com.hyperdict.app.data.model.WordDefinition
import com.hyperdict.app.data.repository.WordRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val definition: WordDefinition, val isOffline: Boolean) : UiState {
        constructor(definition: WordDefinition) : this(definition, true)
    }
    data class Error(val message: String) : UiState
}

class DictionaryViewModel(
    private val repository: WordRepository,
    private val context: Context
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var suggestions by mutableStateOf<List<WordSuggestion>>(emptyList())
        private set

    var downloadProgress by mutableStateOf<DownloadProgress>(
        if (DatabaseDownloader.isDatabaseDownloaded(context))
            DownloadProgress(DownloadProgress.Status.SUCCESS)
        else
            DownloadProgress(DownloadProgress.Status.NOT_STARTED)
    )
        private set

    fun onQueryChange(query: String) {
        searchQuery = query
        // Update suggestions as user types
        updateSuggestions(query)
    }

    private fun updateSuggestions(query: String) {
        if (query.isBlank()) {
            suggestions = emptyList()
            return
        }

        suggestions = repository.getSuggestions(query, limit = 10)
    }

    fun selectSuggestion(suggestion: WordSuggestion) {
        searchQuery = suggestion.word
        searchWord(suggestion.word)
    }

    fun searchWord(word: String) {
        if (word.isBlank()) {
            uiState = UiState.Idle
            suggestions = emptyList()
            return
        }

        viewModelScope.launch {
            uiState = UiState.Loading
            suggestions = emptyList()

            repository.lookupWord(word).fold(
                onSuccess = { definition ->
                    // Check if it's from offline dictionary (has "翻译" meaning)
                    val isOffline = definition.meanings.any { it.partOfSpeech == "翻译" }
                    uiState = UiState.Success(definition, isOffline)
                    searchQuery = definition.word
                },
                onFailure = { error ->
                    uiState = UiState.Error(error.message ?: "Unknown error occurred")
                }
            )
        }
    }

    fun startDatabaseDownload() {
        viewModelScope.launch {
            DatabaseDownloader.downloadDatabase(context).collectLatest { progress ->
                downloadProgress = progress
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
        }
    }
}

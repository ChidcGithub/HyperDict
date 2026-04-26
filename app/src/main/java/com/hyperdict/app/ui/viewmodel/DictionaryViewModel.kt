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
import com.hyperdict.app.data.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class Success(val definition: WordDefinition) : UiState {
        val isOffline: Boolean get() = definition.isOffline
    }
    data class Error(val message: String) : UiState
}

sealed interface Screen {
    object Home : Screen
    object Settings : Screen
}

class DictionaryViewModel(
    private val repository: WordRepository,
    private val context: Context
) : ViewModel() {

    val settings = AppSettings(context)

    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

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

    private var searchJob: Job? = null
    private var suggestionJob: Job? = null

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    fun goBack() {
        if (currentScreen != Screen.Home) {
            currentScreen = Screen.Home
        }
    }

    fun onQueryChange(query: String) {
        searchQuery = query
        // Update suggestions as user types
        if (settings.autoSearch) {
            updateSuggestions(query)
        }
    }

    private fun updateSuggestions(query: String) {
        if (query.isBlank()) {
            suggestions = emptyList()
            return
        }

        // Cancel previous suggestion request to avoid race conditions
        suggestionJob?.cancel()
        suggestionJob = viewModelScope.launch {
            try {
                suggestions = withContext(Dispatchers.IO) {
                    repository.getSuggestions(query, limit = 10)
                }
            } catch (e: Exception) {
                // Silently fail for suggestions
                suggestions = emptyList()
            }
        }
    }

    fun selectSuggestion(suggestion: WordSuggestion) {
        searchQuery = suggestion.word
        searchWord(suggestion.word)
    }

    fun searchWord(word: String) {
        val trimmedWord = word.trim()
        if (trimmedWord.isBlank()) {
            uiState = UiState.Idle
            suggestions = emptyList()
            return
        }

        // Cancel previous search to avoid race conditions
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            uiState = UiState.Loading
            suggestions = emptyList()

            try {
                repository.lookupWord(trimmedWord).fold(
                    onSuccess = { definition ->
                        uiState = UiState.Success(definition)
                        searchQuery = definition.word
                    },
                    onFailure = { error ->
                        uiState = UiState.Error(error.message ?: "Unknown error occurred")
                    }
                )
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                uiState = UiState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun startDatabaseDownload() {
        viewModelScope.launch {
            try {
                DatabaseDownloader.downloadDatabase(context).collectLatest { progress ->
                    downloadProgress = progress
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                downloadProgress = DownloadProgress(
                    status = DownloadProgress.Status.FAILED,
                    error = "Download failed: ${e.message}"
                )
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                repository.clearCache()
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        suggestionJob?.cancel()
    }
}

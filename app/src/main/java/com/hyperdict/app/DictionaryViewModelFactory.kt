package com.hyperdict.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hyperdict.app.data.repository.WordRepository
import com.hyperdict.app.ui.viewmodel.DictionaryViewModel

class DictionaryViewModelFactory(
    private val repository: WordRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DictionaryViewModel::class.java)) {
            return DictionaryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

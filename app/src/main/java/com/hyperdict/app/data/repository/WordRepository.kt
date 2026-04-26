package com.hyperdict.app.data.repository

import android.util.Log
import com.google.gson.Gson
import com.hyperdict.app.data.local.OfflineDictionarySource
import com.hyperdict.app.data.local.WordDao
import com.hyperdict.app.data.local.WordEntity
import com.hyperdict.app.data.local.WordSuggestion
import com.hyperdict.app.data.model.WordDefinition
import com.hyperdict.app.data.remote.DictionaryApi
import com.hyperdict.app.data.remote.toWordDefinition

private const val TAG = "WordRepository"
private const val CACHE_EXPIRY_MS = 7 * 24 * 60 * 60 * 1000L // 7 days

class WordRepository(
    private val api: DictionaryApi,
    private val dao: WordDao,
    private val offlineSource: OfflineDictionarySource
) {
    private val gson = Gson()

    /**
     * Look up a word with offline-first strategy
     */
    suspend fun lookupWord(word: String): Result<WordDefinition> {
        val trimmedWord = word.trim().lowercase()

        if (trimmedWord.isEmpty()) {
            return Result.failure(IllegalArgumentException("Word cannot be empty"))
        }

        // Try offline dictionary first
        try {
            val offlineResult = offlineSource.lookupWord(trimmedWord)
            if (offlineResult != null) {
                Log.d(TAG, "Offline dictionary hit for: $trimmedWord")
                return Result.success(offlineResult)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Offline dictionary error, trying cache: $trimmedWord", e)
        }

        // Try local cache from online API
        val cachedEntity = dao.getWord(trimmedWord)
        if (cachedEntity != null) {
            val isExpired = System.currentTimeMillis() - cachedEntity.cachedAt > CACHE_EXPIRY_MS

            if (!isExpired) {
                Log.d(TAG, "Cache hit for: $trimmedWord")
                return Result.success(entityToDefinition(cachedEntity))
            } else {
                Log.d(TAG, "Cache expired for: $trimmedWord, will refresh")
            }
        }

        // Fetch from network
        return try {
            Log.d(TAG, "Fetching from network: $trimmedWord")
            val response = api.getWord(word = trimmedWord)

            if (response.isSuccessful && response.body() != null) {
                val definition = response.body()!!.toWordDefinition()

                // Cache the result
                dao.insertWord(definitionToEntity(definition))

                Result.success(definition)
            } else {
                // If network fails but we have expired cache, return it as fallback
                if (cachedEntity != null) {
                    Log.d(TAG, "Network failed, returning expired cache for: $trimmedWord")
                    Result.success(entityToDefinition(cachedEntity))
                } else {
                    Result.failure(Exception("Word not found (HTTP ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network error for: $trimmedWord", e)

            // Fallback to expired cache if available
            if (cachedEntity != null) {
                Log.d(TAG, "Network error, returning expired cache for: $trimmedWord")
                Result.success(entityToDefinition(cachedEntity))
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Get word suggestions for autocomplete
     */
    fun getSuggestions(query: String, limit: Int = 10): List<WordSuggestion> {
        return if (query.isNotBlank()) {
            offlineSource.searchWords(query.trim().lowercase(), limit)
        } else {
            emptyList()
        }
    }

    suspend fun clearCache() {
        dao.clearAll()
    }

    private fun entityToDefinition(entity: WordEntity): WordDefinition {
        val meaningsJson = entity.meaningsJson
        val meaningsType = object : com.google.gson.reflect.TypeToken<List<com.hyperdict.app.data.model.Meaning>>() {}.type
        val meanings = gson.fromJson<List<com.hyperdict.app.data.model.Meaning>>(meaningsJson, meaningsType)

        return WordDefinition(
            word = entity.word,
            phonetic = entity.phonetic,
            phoneticUs = entity.phoneticUs,
            phoneticUk = entity.phoneticUk,
            meanings = meanings,
            isOffline = false,
            cachedAt = entity.cachedAt
        )
    }

    private fun definitionToEntity(definition: WordDefinition): WordEntity {
        val meaningsJson = gson.toJson(definition.meanings)

        return WordEntity(
            word = definition.word,
            phonetic = definition.phonetic,
            phoneticUs = definition.phoneticUs,
            phoneticUk = definition.phoneticUk,
            meaningsJson = meaningsJson,
            cachedAt = definition.cachedAt
        )
    }
}

package com.hyperdict.app.data.local

import com.hyperdict.app.data.model.DefinitionEntry
import com.hyperdict.app.data.model.Meaning
import com.hyperdict.app.data.model.WordDefinition

class OfflineDictionarySource(
    private val database: DictionaryDatabase
) {
    /**
     * Look up a word in the offline dictionary
     */
    fun lookupWord(word: String): WordDefinition? {
        return try {
            val result = database.lookupWord(word) ?: return null
            parseDictionaryResult(word, result)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Search for words starting with the given query
     */
    fun searchWords(query: String, limit: Int = 20): List<WordSuggestion> {
        return try {
            val results = database.searchWords(query, limit)
            results.map {
                WordSuggestion(
                    word = it["word"] ?: "",
                    definition = it["definition"] ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseDictionaryResult(word: String, data: Map<String, String>): WordDefinition {
        val definition = data["definition"] ?: ""
        val translation = data["translation"] ?: ""
        val phonetic = data["phonetic"]

        // Parse the definition into structured meanings
        val meanings = parseDefinition(definition)

        // If we have Chinese translation, add it as a special meaning
        val finalMeanings = if (translation.isNotBlank()) {
            listOf(
                Meaning(
                    partOfSpeech = "翻译",
                    definitions = listOf(
                        DefinitionEntry(
                            definition = translation,
                            example = null
                        )
                    )
                )
            ) + meanings
        } else {
            meanings
        }

        return WordDefinition(
            word = word,
            phonetic = phonetic,
            phoneticUs = null,
            phoneticUk = null,
            meanings = finalMeanings
        )
    }

    private fun parseDefinition(definition: String): List<Meaning> {
        if (definition.isBlank()) return emptyList()

        val entries = definition.split("\n").filter { it.isNotBlank() }

        return if (entries.isNotEmpty()) {
            listOf(
                Meaning(
                    partOfSpeech = "释义",
                    definitions = entries.map { entry ->
                        DefinitionEntry(
                            definition = entry.trim(),
                            example = null
                        )
                    }
                )
            )
        } else {
            emptyList()
        }
    }
}

data class WordSuggestion(
    val word: String,
    val definition: String
)

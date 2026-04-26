package com.hyperdict.app.data.model

data class WordDefinition(
    val word: String,
    val phonetic: String?,
    val phoneticUs: String?,
    val phoneticUk: String?,
    val meanings: List<Meaning>,
    val isOffline: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis()
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<DefinitionEntry>
)

data class DefinitionEntry(
    val definition: String,
    val example: String?,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)

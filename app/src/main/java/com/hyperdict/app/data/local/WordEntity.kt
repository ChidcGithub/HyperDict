package com.hyperdict.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_cache")
data class WordEntity(
    @PrimaryKey
    val word: String,
    val phonetic: String?,
    val phoneticUs: String?,
    val phoneticUk: String?,
    val meaningsJson: String,
    val cachedAt: Long
)

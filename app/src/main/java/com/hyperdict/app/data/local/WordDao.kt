package com.hyperdict.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM word_cache WHERE word = :word")
    suspend fun getWord(word: String): WordEntity?

    @Query("SELECT * FROM word_cache WHERE word = :word")
    fun getWordFlow(word: String): Flow<WordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Query("DELETE FROM word_cache WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    @Query("SELECT COUNT(*) FROM word_cache")
    suspend fun getCacheCount(): Int

    @Query("DELETE FROM word_cache")
    suspend fun clearAll()
}

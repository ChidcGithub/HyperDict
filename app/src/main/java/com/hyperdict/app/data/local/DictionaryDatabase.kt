package com.hyperdict.app.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class DictionaryDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ecdict.db"
        private const val DATABASE_VERSION = 1
        private const val ASSET_DB_NAME = "dictionary.db"

        @Volatile
        private var INSTANCE: DictionaryDatabase? = null

        fun getInstance(context: Context): DictionaryDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DictionaryDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        fun getDatabasePath(context: Context): File {
            return context.getDatabasePath(DATABASE_NAME)
        }
    }

    init {
        // Copy database from assets if it doesn't exist
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        if (!dbPath.exists()) {
            dbPath.parentFile?.mkdirs()
            copyDatabaseFromAssets(context)
        }
    }

    private fun copyDatabaseFromAssets(context: Context) {
        try {
            val inputStream: InputStream = context.assets.open(ASSET_DB_NAME)
            val outputStream = FileOutputStream(getDatabasePath(context))

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: Exception) {
            throw RuntimeException("Error copying database from assets", e)
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Database is created from assets, no need to create tables
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
        db?.execSQL("DROP TABLE IF EXISTS stardict")
        onCreate(db)
    }

    /**
     * Look up a word in the dictionary
     * @param word The word to look up
     * @return A map containing word data or null if not found
     */
    fun lookupWord(word: String): Map<String, String>? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM stardict WHERE word = ? LIMIT 1",
            arrayOf(word.lowercase())
        )

        return try {
            if (cursor.moveToFirst()) {
                val result = mutableMapOf<String, String>()
                for (i in 0 until cursor.columnCount) {
                    val columnName = cursor.getColumnName(i)
                    val value = if (cursor.isNull(i)) "" else cursor.getString(i)
                    result[columnName] = value
                }
                result
            } else {
                null
            }
        } finally {
            cursor.close()
        }
    }

    /**
     * Search for words matching a query
     * @param query The search query
     * @param limit Maximum number of results
     * @return List of matching words
     */
    fun searchWords(query: String, limit: Int = 20): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT word, definition FROM stardict WHERE word LIKE ? LIMIT ?",
            arrayOf("${query.lowercase()}%", limit.toString())
        )

        val results = mutableListOf<Map<String, String>>()
        try {
            while (cursor.moveToNext()) {
                val word = if (cursor.isNull(0)) "" else cursor.getString(0)
                val definition = if (cursor.isNull(1)) "" else cursor.getString(1)
                results.add(mapOf(
                    "word" to word,
                    "definition" to definition
                ))
            }
        } finally {
            cursor.close()
        }
        return results
    }
}

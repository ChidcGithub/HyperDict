package com.hyperdict.app.di

import android.content.Context
import com.hyperdict.app.data.local.DictionaryDatabase
import com.hyperdict.app.data.local.OfflineDictionarySource
import com.hyperdict.app.data.local.WordDatabase
import com.hyperdict.app.data.remote.DictionaryApi
import com.hyperdict.app.data.repository.WordRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceLocator {
    private const val BASE_URL = "https://api.dictionaryapi.dev/"

    @Volatile
    private var database: WordDatabase? = null

    @Volatile
    private var dictionaryDatabase: DictionaryDatabase? = null

    @Volatile
    private var repository: WordRepository? = null

    fun getRepository(context: Context): WordRepository {
        return repository ?: synchronized(this) {
            repository ?: createRepository(context).also { repository = it }
        }
    }

    private fun createRepository(context: Context): WordRepository {
        val db = WordDatabase.getDatabase(context)
        val api = createApi()
        val offlineSource = createOfflineDictionarySource(context)
        return WordRepository(api, db.wordDao(), offlineSource)
    }

    private fun createOfflineDictionarySource(context: Context): OfflineDictionarySource {
        val db = DictionaryDatabase.getInstance(context)
        return OfflineDictionarySource(db)
    }

    private fun createApi(): DictionaryApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .apply {
                if (com.hyperdict.app.BuildConfig.DEBUG) {
                    val loggingInterceptor = HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    }
                    addInterceptor(loggingInterceptor)
                }
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(DictionaryApi::class.java)
    }
}

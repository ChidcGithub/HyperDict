package com.hyperdict.app.data.remote

import com.hyperdict.app.data.model.DefinitionEntry
import com.hyperdict.app.data.model.Meaning
import com.hyperdict.app.data.model.WordDefinition
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class FreeDictionaryResponse(
    val word: String,
    val phonetics: List<PhoneticResponse>?,
    val meanings: List<MeaningResponse>
)

data class PhoneticResponse(
    val text: String?,
    val audio: String?
)

data class MeaningResponse(
    val partOfSpeech: String,
    val definitions: List<DefinitionResponse>
)

data class DefinitionResponse(
    val definition: String,
    val example: String?
)

interface DictionaryApi {
    @GET("api/{version}/entries/{language}/{word}")
    suspend fun getWord(
        @Path("version") version: String = "v2",
        @Path("language") language: String = "en",
        @Path("word") word: String
    ): Response<List<FreeDictionaryResponse>>
}

fun List<FreeDictionaryResponse>.toWordDefinition(): WordDefinition {
    val first = first()
    val phonetics = first.phonetics ?: emptyList()

    return WordDefinition(
        word = first.word,
        phonetic = phonetics.firstOrNull { it.text != null }?.text,
        phoneticUs = phonetics.firstOrNull { it.text?.contains("us") == true }?.text,
        phoneticUk = phonetics.firstOrNull { it.text?.contains("uk") == true }?.text,
        meanings = first.meanings.map { meaning ->
            Meaning(
                partOfSpeech = meaning.partOfSpeech,
                definitions = meaning.definitions.map { def ->
                    DefinitionEntry(
                        definition = def.definition,
                        example = def.example
                    )
                }
            )
        }
    )
}

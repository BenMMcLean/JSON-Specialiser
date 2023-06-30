package cl.benm.jsons.config

import com.google.gson.JsonElement

interface FileAccessStrategy {

    suspend fun read(name: String): JsonElement

}
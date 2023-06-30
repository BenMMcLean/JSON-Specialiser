package cl.benm.jsons.command

import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.context.ContextGuarded
import com.google.gson.JsonElement
import com.google.gson.JsonObject

interface Command: ContextGuarded {

    val key: String
    suspend fun process(node: JsonElement, context: CompilerContext): JsonElement?

}
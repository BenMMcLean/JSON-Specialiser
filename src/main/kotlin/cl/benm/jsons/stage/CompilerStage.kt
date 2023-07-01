package cl.benm.jsons.stage

import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.context.ContextGuarded
import com.google.gson.JsonElement

interface CompilerStage: ContextGuarded {

    suspend fun process(document: JsonElement, context: CompilerContext): JsonElement?

}
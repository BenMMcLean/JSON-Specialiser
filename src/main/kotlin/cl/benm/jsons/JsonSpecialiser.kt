package cl.benm.jsons

import cl.benm.jsons.context.CompilerContext
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser

class JsonSpecialiser(
    private val gson: Gson = Gson()
) {

    suspend fun compile(document: JsonElement, context: CompilerContext): JsonElement? {
        var working = document
        for (stage in context.config.stages) {
            if (!stage.canRunInContext(context)) continue
            working = stage.process(document, context) ?: return null
        }
        return working
    }

    suspend fun compile(document: String, context: CompilerContext): String? {
        val tree = JsonParser.parseString(document)
        return compile(tree, context)?.let { gson.toJson(it) }
    }

}
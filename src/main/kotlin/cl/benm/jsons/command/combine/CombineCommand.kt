package cl.benm.jsons.command.combine

import cl.benm.jsons.command.Command
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Flattens two arrays or objects
 */
class CombineCommand: Command {

    override val key: String = "combine"

    override suspend fun process(node: JsonElement, context: CompilerContext): JsonElement? {
        if (!node.isJsonArray) throw CompilerException("Combine must be provided an array")
        val aNode = node.asJsonArray
        return when {
            aNode.all { it.isJsonArray } -> processArray(aNode)
            aNode.all { it.isJsonObject } -> processObject(aNode)
            else -> throw CompilerException("Expecting all children of combine array to be either array xor object")
        }
    }

    private fun processArray(node: JsonArray): JsonArray {
        return JsonArray().apply {
            node.map {
                it.asJsonArray
            }.forEach { addAll(it) }
        }
    }

    private fun processObject(node: JsonArray): JsonObject {
        val obj = JsonObject()
        for (item in node) {
            for (element in item.asJsonObject.asMap()) {
                obj.add(element.key, element.value)
            }
        }
        return obj
    }

    override fun canRunInContext(context: CompilerContext): Boolean {
        return true
    }
}
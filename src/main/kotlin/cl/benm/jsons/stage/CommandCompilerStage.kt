package cl.benm.jsons.stage

import cl.benm.jsons.command.Command
import cl.benm.jsons.command.combine.CombineCommand
import cl.benm.jsons.command.include.IncludeCommand
import cl.benm.jsons.command.selector.SelectorCommand
import cl.benm.jsons.context.CompilerContext
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class CommandCompilerStage(
    private val commands: List<Command> = listOf(
        CombineCommand(),
        IncludeCommand(),
        SelectorCommand()
    )
): CompilerStage {

    companion object {
        const val COMMAND_CHAR = "#"
    }

    override suspend fun process(document: JsonElement, context: CompilerContext): JsonElement? {
        return when {
            document.isJsonObject -> processObject(document.asJsonObject, context)
            document.isJsonArray -> processArray(document.asJsonArray, context)
            else -> document
        }
    }

    private suspend fun processArray(document: JsonArray, context: CompilerContext): JsonElement {
        val out = JsonArray()
        for (item in document) {
            process(item, context)?.let { out.add(it) }
        }
        return out
    }

    private suspend fun processObject(document: JsonObject, context: CompilerContext): JsonElement? {
        // Check for commands
        for (command in commands) {
            val fullKey = "${COMMAND_CHAR}${command.key}"

            if (!document.has(fullKey)) continue
            return command.process(document.get(fullKey), context)?.let {
                process(it, context)
            }
        }

        // Process children
        val out = JsonObject()
        for (element in document.asMap()) {
            process(element.value, context)?.let { out.add(element.key, it) }
        }
        return out
    }

    override fun canRunInContext(context: CompilerContext): Boolean {
        return true
    }
}
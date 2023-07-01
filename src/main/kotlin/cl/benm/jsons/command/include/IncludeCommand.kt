package cl.benm.jsons.command.include

import cl.benm.jsons.command.Command
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import cl.benm.jsons.exception.ContextException
import com.google.gson.JsonElement

/**
 * Includes and inserts in-place another document
 */
class IncludeCommand: Command {

    override val key: String = "include"

    override suspend fun process(node: JsonElement, context: CompilerContext): JsonElement? {
        if (!node.isJsonObject) throw CompilerException("Expecting the child of an include to be an object")
        if (!node.asJsonObject.has("file")) throw CompilerException("Include must specify a file")
        if (context.config.fileAccessStrategy == null) throw ContextException("Expecting a FileAccessStrategy")

        return context.config.fileAccessStrategy
            ?.read(node.asJsonObject.get("file").asString)
    }

    override fun canRunInContext(context: CompilerContext): Boolean {
        return context.config.fileAccessStrategy != null
    }
}
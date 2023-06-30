package cl.benm.jsons.command.include

import cl.benm.jsons.command.Command
import cl.benm.jsons.context.CompilerContext
import com.google.gson.JsonElement

class IncludeCommand: Command {

    override val key: String = "include"

    override fun process(node: JsonElement, context: CompilerContext): JsonElement? {

        return node
    }

    override fun canRunInContext(context: CompilerContext): Boolean {
        return true
    }
}
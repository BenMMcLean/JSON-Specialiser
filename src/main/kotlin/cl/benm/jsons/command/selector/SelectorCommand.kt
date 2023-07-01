package cl.benm.jsons.command.selector

import cl.benm.jsons.command.Command
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import com.google.gson.JsonElement
import com.google.gson.JsonObject

/**
 * Processes a list of options and selects the first one that matches the given conditions.
 */
class SelectorCommand(
    private val conditions: List<SelectorCondition> = listOf(
        ClaimSelectorCondition()
    )
): Command {

    override val key: String = "selector"

    override suspend fun process(node: JsonElement, context: CompilerContext): JsonElement? {
        if (!node.isJsonArray) throw CompilerException("Expecting the child of the selector to be an array")

        for (option in node.asJsonArray) {
            var anyNotMatch = false
            if (option !is JsonObject) throw CompilerException("Expecting elements of the array to be an object")
            for (condition in conditions) {
                if (option.has(condition.key)) {
                    anyNotMatch = anyNotMatch || condition.evaluate(option.get(condition.key), context)
                }
            }

            if (!anyNotMatch) {
                return if (option.has("child"))
                    option.get("child")
                else
                    null
            }
        }

        return null
    }

    override fun canRunInContext(context: CompilerContext): Boolean {
        return !context.final
    }
}
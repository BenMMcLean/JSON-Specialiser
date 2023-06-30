package cl.benm.jsons.command.selector

import cl.benm.jsons.context.ClaimsCompilerContext
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import cl.benm.jsons.exception.ContextException
import com.google.gson.JsonArray
import com.google.gson.JsonElement

class ClaimSelectorCondition: SelectorCondition {

    override val key: String = "claims"

    override fun evaluate(element: JsonElement, context: CompilerContext): Boolean {
        if (!element.isJsonArray) throw CompilerException("Expecting a 2D array of claims")
        if (context !is ClaimsCompilerContext) throw ContextException("ClaimSelectorCondition requires a ClaimsCompilerContext")

        for (e in element.asJsonArray) {
            if (e.isJsonArray) throw CompilerException("Expecting a 2D array of claims")
            if (!e.asJsonArray.all { it.isJsonPrimitive }) throw CompilerException("Expecting claims to be a string")
            if (context.claims.containsAll(e.asJsonArray.map { it.asString })) return true
        }

        return false
    }
}
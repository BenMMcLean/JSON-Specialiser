package cl.benm.jsons.command.selector

import cl.benm.jsons.context.ClaimsCompilerContext
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import cl.benm.jsons.exception.ContextException
import com.google.gson.JsonArray
import com.google.gson.JsonElement

/**
 * ClaimSelectorCondition evaluates a 2D array of strings against a list of "claims" a client would possess.
 * It evaluates the top level as a set of conditions that are or-d together, and the second level as a set of ands.
 *
 * Example:
 * A client has the claims ["claim1", "claim2"]
 * [["claim1", "claim3"], ["claim4"]] - Would not match, since the user doesn't have both claim (1 && 4) || 5.
 * [["claim1"], ["claim3"]] - Would match, since the user has claim1
 */
class ClaimSelectorCondition: SelectorCondition {

    override val key: String = "claims"

    override fun evaluate(element: JsonElement, context: CompilerContext): Boolean {
        if (!element.isJsonArray) throw CompilerException("Expecting a 2D array of claims")
        if (context !is ClaimsCompilerContext) throw ContextException("ClaimSelectorCondition requires a ClaimsCompilerContext")

        for (e in element.asJsonArray) {
            if (!e.isJsonArray) throw CompilerException("Expecting a 2D array of claims")
            if (!e.asJsonArray.all { it.isJsonPrimitive && it.asJsonPrimitive.isString }) throw CompilerException("Expecting claims to be a string")
            if (context.claims.containsAll(e.asJsonArray.map { it.asString })) return true
        }

        return false
    }
}
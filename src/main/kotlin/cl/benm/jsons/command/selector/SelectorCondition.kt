package cl.benm.jsons.command.selector

import cl.benm.jsons.context.CompilerContext
import com.google.gson.JsonElement

interface SelectorCondition {

    val key: String
    fun evaluate(element: JsonElement, context: CompilerContext): Boolean

}
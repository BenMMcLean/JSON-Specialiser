package cl.benm.jsons.context

interface ContextGuarded {

    fun canRunInContext(context: CompilerContext): Boolean

}
package cl.benm.jsons.context.concrete

import cl.benm.jsons.config.CompilerConfig
import cl.benm.jsons.context.ClaimsCompilerContext
import cl.benm.jsons.context.CompilerContext

class DefaultFinalCompilerContext(
    override val claims: List<String>,
    override val config: CompilerConfig
): CompilerContext, ClaimsCompilerContext {

    override val final: Boolean = true

}
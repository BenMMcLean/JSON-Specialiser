package cl.benm.jsons.context.concrete

import cl.benm.jsons.config.CompilerConfig
import cl.benm.jsons.context.ClaimsCompilerContext
import cl.benm.jsons.context.CompilerContext

class DefaultIntermediateCompilerContext(
    override val config: CompilerConfig
): CompilerContext {

    override val final: Boolean = false

}
package cl.benm.jsons.context

import cl.benm.jsons.config.CompilerConfig

interface CompilerContext {

    val final: Boolean
    val config: CompilerConfig

}

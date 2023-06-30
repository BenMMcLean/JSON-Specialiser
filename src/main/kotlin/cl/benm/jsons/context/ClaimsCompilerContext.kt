package cl.benm.jsons.context

interface ClaimsCompilerContext: CompilerContext {

    val claims: List<String>

}
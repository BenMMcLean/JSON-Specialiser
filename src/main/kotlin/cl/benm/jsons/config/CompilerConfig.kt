package cl.benm.jsons.config

import cl.benm.jsons.stage.CompilerStage

class CompilerConfig(
    val stages: List<CompilerStage>,
    val fileAccessStrategy: FileAccessStrategy?
)
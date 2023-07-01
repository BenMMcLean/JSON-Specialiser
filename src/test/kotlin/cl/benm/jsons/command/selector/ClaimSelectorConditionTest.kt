package cl.benm.jsons.command.selector

import cl.benm.jsons.config.CompilerConfig
import cl.benm.jsons.context.ClaimsCompilerContext
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import cl.benm.jsons.exception.ContextException

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ClaimSelectorConditionTest {

    @Test
    fun `evaluate returns true when claims match`() {
        // Arrange
        val condition = ClaimSelectorCondition()
        val claimsContext = object : ClaimsCompilerContext {
            override val final: Boolean = false
            override val config: CompilerConfig get() = throw NotImplementedError()
            override val claims: List<String> = listOf("claim1", "claim2")
        }
        val element = JsonParser().parse("[[\"claim1\", \"claim2\"], [\"claim3\"]]").asJsonArray

        // Act
        val result = condition.evaluate(element, claimsContext)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `evaluate returns false when claims do not match`() {
        // Arrange
        val condition = ClaimSelectorCondition()
        val claimsContext = object : ClaimsCompilerContext {
            override val final: Boolean = false
            override val config: CompilerConfig get() = throw NotImplementedError()
            override val claims: List<String> = listOf("claim1", "claim2")
        }
        val element = JsonParser.parseString("[[\"claim1\", \"claim3\"], [\"claim4\"]]").asJsonArray

        // Act
        val result = condition.evaluate(element, claimsContext)

        // Assert
        assertFalse(result)
    }

    @Test
    fun `evaluate throws CompilerException when element is not a 2D array`() {
        // Arrange
        val condition = ClaimSelectorCondition()
        val claimsContext = object : ClaimsCompilerContext {
            override val final: Boolean = false
            override val config: CompilerConfig get() = throw NotImplementedError()
            override val claims: List<String> = listOf("claim1", "claim2")
        }
        val element = JsonParser.parseString("[\"claim1\", \"claim2\"]").asJsonArray

        // Act
        assertThrows<CompilerException> { condition.evaluate(element, claimsContext) }

        // CompilerException expected
    }

    @Test
    fun `evaluate throws ContextException when context is not ClaimsCompilerContext`() {
        // Arrange
        val condition = ClaimSelectorCondition()
        val context = object : CompilerContext {
            override val final: Boolean = false
            override val config: CompilerConfig get() = throw NotImplementedError()
        }
        val element = JsonParser.parseString("[[\"claim1\", \"claim2\"]]").asJsonArray

        // Act
        assertThrows<ContextException> { condition.evaluate(element, context) }

        // ContextException expected
    }

    @Test
    fun `evaluate throws CompilerException when claims contain non-string values`() {
        // Arrange
        val condition = ClaimSelectorCondition()
        val claimsContext = object : ClaimsCompilerContext {
            override val final: Boolean = false
            override val config: CompilerConfig get() = throw NotImplementedError()
            override val claims: List<String> = listOf("claim1", "claim2")
        }
        val element = JsonParser.parseString("[[\"claim1\", 123], [\"claim2\"]]").asJsonArray

        // Act
        assertThrows<CompilerException> { condition.evaluate(element, claimsContext) }

        // CompilerException expected
    }
}

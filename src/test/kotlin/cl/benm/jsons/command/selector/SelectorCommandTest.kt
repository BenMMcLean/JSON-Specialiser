package cl.benm.jsons.command.selector

import cl.benm.jsons.config.CompilerConfig
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.context.concrete.DefaultFinalCompilerContext
import cl.benm.jsons.exception.CompilerException
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import org.junit.jupiter.api.Assertions.*
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.Mockito.*

class SelectorCommandTest {

    @Test
    fun `process should return null when node is not a JSON array`() {
        // Arrange
        val command = SelectorCommand()

        // Act & Assert
        assertThrows(CompilerException::class.java) {
            runBlocking { command.process(
                JsonObject(),
                DefaultFinalCompilerContext(listOf(), CompilerConfig(listOf(), null))
            )}
        }
    }

    @Test
    fun `process should return null when all conditions fail`() {
        // Arrange
        val node = JsonArray().apply {
            add(JsonObject().apply {
                add("child", JsonPrimitive("child1"))
                add("key", JsonPrimitive("test"))
            })
            add(JsonObject().apply {
                add("child", JsonPrimitive("child2"))
                add("key", JsonPrimitive("test2"))
            })
        }

        val compilerContext = mock(CompilerContext::class.java)
        val condition = mock(SelectorCondition::class.java)
        `when`(condition.key).thenReturn("key")
        `when`(condition.evaluate(any(), any())).thenReturn(false)

        val command = SelectorCommand(
            listOf(condition)
        )

        // Act
        val result = runBlocking { command.process(node, compilerContext) }

        // Assert
        assertNull(result)
        verify(compilerContext, never()).final
    }

    @Test
    fun `process should return child element when any condition matches`() {
        // Arrange
        val node = JsonArray().apply {
            add(JsonObject().apply {
                add("child", JsonPrimitive("child1"))
                add("key", JsonPrimitive("test"))
            })
            add(JsonObject().apply {
                add("child", JsonPrimitive("child2"))
                add("key", JsonPrimitive("test2"))
            })
        }

        val compilerContext = mock(CompilerContext::class.java)
        val condition = object : SelectorCondition {
            override val key: String = "key"
            override fun evaluate(element: JsonElement, context: CompilerContext): Boolean {
                return element.isJsonPrimitive
                        && element.asJsonPrimitive.isString
                        && element.asJsonPrimitive.asString == "test"
            }
        }

        val command = SelectorCommand(
            listOf(condition)
        )

        // Act
        val result = runBlocking { command.process(node, compilerContext) }

        // Assert
        assertNotNull(result)
        assertTrue(result is JsonElement)
        verify(compilerContext, never()).final
    }

    @Test
    fun `canRunInContext should return true when context is not final`() {
        // Arrange
        val command = SelectorCommand()
        val compilerContext = mock(CompilerContext::class.java)
        `when`(compilerContext.final).thenReturn(false)

        // Act
        val result = command.canRunInContext(compilerContext)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `canRunInContext should return false when context is final`() {
        // Arrange
        val command = SelectorCommand()
        val compilerContext = mock(CompilerContext::class.java)
        `when`(compilerContext.final).thenReturn(true)

        // Act
        val result = command.canRunInContext(compilerContext)

        // Assert
        assertFalse(result)
    }
}

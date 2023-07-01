package cl.benm.jsons.command.combine

import cl.benm.jsons.command.Command
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.Mockito.*
import org.mockito.kotlin.stub

class CombineCommandTest {

    @Test
    fun `process should throw CompilerException when node is not a JSON array`() {
        // Arrange
        val command = CombineCommand()
        val node = mock(JsonElement::class.java)
        `when`(node.isJsonArray).thenReturn(false)

        // Act & Assert
        assertThrows(CompilerException::class.java) {
            runBlocking { command.process(node, mock(CompilerContext::class.java)) }
        }
    }

    @Test
    fun `process should throw CompilerException when children of node are not arrays or objects`() {
        // Arrange
        val command = CombineCommand()
        val array = JsonArray().apply {
            add(JsonObject())
            add(JsonArray())
        }

        // Act & Assert
        assertThrows(CompilerException::class.java) {
            runBlocking { command.process(array, mock(CompilerContext::class.java)) }
        }
    }

    @Test
    fun `process should return combined array when all children of node are arrays`() {
        // Arrange
        val command = CombineCommand()
        val input = JsonArray().apply {
            add(JsonArray().apply {
                add(JsonPrimitive("test"))
            })
            add(JsonArray().apply {
                add(JsonPrimitive("test2"))
            })
        }
        // Act
        val result = runBlocking { command.process(input, mock(CompilerContext::class.java)) }

        // Assert
        assertNotNull(result)
        assertTrue(result is JsonArray)
        assertEquals(JsonArray().apply {
            add(JsonPrimitive("test"))
            add(JsonPrimitive("test2"))
        }, result)
    }

    @Test
    fun `process should return combined object when all children of node are objects`() {
        // Arrange
        val command = CombineCommand()
        val input = JsonArray().apply {
            add(JsonObject().apply {
                add("key1", JsonPrimitive("test"))
            })
            add(JsonObject().apply {
                add("key2", JsonPrimitive("test2"))
            })
        }
        // Act
        val result = runBlocking { command.process(input, mock(CompilerContext::class.java)) }

        // Assert
        assertNotNull(result)
        assertTrue(result is JsonObject)
        assertEquals(JsonObject().apply {
            add("key1", JsonPrimitive("test"))
            add("key2", JsonPrimitive("test2"))
        }, result)
    }

    @Test
    fun `canRunInContext should always return true`() {
        // Arrange
        val command = CombineCommand()
        val compilerContext = mock(CompilerContext::class.java)

        // Act
        val result = command.canRunInContext(compilerContext)

        // Assert
        assertTrue(result)
    }
}

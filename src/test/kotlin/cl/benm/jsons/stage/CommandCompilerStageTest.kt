package cl.benm.jsons.stage

import cl.benm.jsons.command.Command
import cl.benm.jsons.context.CompilerContext
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.stub
import org.mockito.kotlin.any

class CommandCompilerStageTest {

    @Test
    fun `process should return the input document as is when it is not an object or an array`() {
        // Arrange
        val stage = CommandCompilerStage()
        val document = mock(JsonElement::class.java)
        `when`(document.isJsonObject).thenReturn(false)
        `when`(document.isJsonArray).thenReturn(false)

        // Act
        val result = runBlocking { stage.process(document, mock(CompilerContext::class.java)) }

        // Assert
        assertEquals(document, result)
    }

    @Test
    fun `process should return the processed result when an object is provided`() {
        // Arrange
        val processed = JsonPrimitive("test")
        val command = mock(Command::class.java).stub {
            on { key }.thenReturn("test")
            on { canRunInContext(any()) }.thenReturn(true)
            onBlocking { process(any(), any()) }.thenReturn(processed)
        }
        val document = JsonObject().apply {
            add("#test", JsonObject())
        }
        val stage = CommandCompilerStage(
            listOf(command)
        )

        // Act
        val result = runBlocking { stage.process(document, mock(CompilerContext::class.java)) }

        // Assert
        assertEquals(processed, result)
    }

    @Test
    fun `process should return the processed result when an array is provided`() {
        // Arrange
        val processed = JsonPrimitive("test")
        val command = mock(Command::class.java).stub {
            on { key }.thenReturn("test")
            on { canRunInContext(any()) }.thenReturn(true)
            onBlocking { process(any(), any()) }.thenReturn(processed)
        }
        val document = JsonArray().apply {
            add(JsonObject().apply {
                add("#test", JsonObject())
            })
        }
        val stage = CommandCompilerStage(
            listOf(command)
        )

        // Act
        val result = runBlocking { stage.process(document, mock(CompilerContext::class.java)) }

        // Assert
        assertEquals(JsonArray().apply { add(processed) }, result)
    }

    @Test
    fun `process should skip stages unable to run in the given context`() {
        // Arrange
        val processed = JsonPrimitive("test")
        val command = mock(Command::class.java).stub {
            on { key }.thenReturn("test")
            on { canRunInContext(any()) }.thenReturn(false)
            onBlocking { process(any(), any()) }.thenReturn(processed)
        }
        val document = JsonObject().apply {
            add("#test", JsonObject())
        }
        val stage = CommandCompilerStage(
            listOf(command)
        )

        // Act
        val result = runBlocking { stage.process(document, mock(CompilerContext::class.java)) }

        // Assert
        assertNotEquals(processed, result)
        assertEquals(document, result)
    }

    @Test
    fun `canRunInContext should always return true`() {
        // Arrange
        val stage = CommandCompilerStage()
        val context = mock(CompilerContext::class.java)

        // Act
        val result = stage.canRunInContext(context)

        // Assert
        assertTrue(result)
    }
}

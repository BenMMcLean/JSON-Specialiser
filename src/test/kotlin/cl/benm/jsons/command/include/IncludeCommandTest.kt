package cl.benm.jsons.command.include

import cl.benm.jsons.command.Command
import cl.benm.jsons.config.CompilerConfig
import cl.benm.jsons.config.FileAccessStrategy
import cl.benm.jsons.context.CompilerContext
import cl.benm.jsons.exception.CompilerException
import cl.benm.jsons.exception.ContextException
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.stub

class IncludeCommandTest {

    @Test
    fun `process should throw CompilerException when node is not a JSON object`() {
        // Arrange
        val command = IncludeCommand()
        val node = mock(JsonElement::class.java)
        `when`(node.isJsonObject).thenReturn(false)

        // Act & Assert
        assertThrows(CompilerException::class.java) {
            runBlocking { command.process(node, mock(CompilerContext::class.java)) }
        }
    }

    @Test
    fun `process should throw CompilerException when node does not have "file" property`() {
        // Arrange
        val command = IncludeCommand()
        val node = mock(JsonElement::class.java)
        val jsonObject = mock(JsonObject::class.java)
        `when`(node.isJsonObject).thenReturn(true)
        `when`(node.asJsonObject).thenReturn(jsonObject)
        `when`(jsonObject.has("file")).thenReturn(false)

        // Act & Assert
        assertThrows(CompilerException::class.java) {
            runBlocking { command.process(node, mock(CompilerContext::class.java)) }
        }
    }

    @Test
    fun `process should throw ContextException when FileAccessStrategy is not available`() {
        // Arrange
        val command = IncludeCommand()
        val node = mock(JsonElement::class.java)
        val jsonObject = mock(JsonObject::class.java)
        `when`(node.isJsonObject).thenReturn(true)
        `when`(node.asJsonObject).thenReturn(jsonObject)
        `when`(jsonObject.has("file")).thenReturn(true)

        val compilerConfig = mock(CompilerConfig::class.java).stub {
            on { fileAccessStrategy }.thenReturn(null)
        }
        val compilerContext = mock(CompilerContext::class.java).stub {
            on { config }.thenReturn(compilerConfig)
        }

        // Act & Assert
        assertThrows(ContextException::class.java) {
            runBlocking { command.process(node, compilerContext) }
        }
    }

    @Test
    fun `process should return null when FileAccessStrategy reads file successfully`() {
        // Arrange
        val command = IncludeCommand()
        val jsonObject = JsonObject().apply {
            add("file", JsonPrimitive("path/to/file.json"))
        }

        val cFileAccessStrategy = mock(FileAccessStrategy::class.java).stub {
            onBlocking { read("path/to/file.json") }.thenReturn(null)
        }
        val compilerConfig = mock(CompilerConfig::class.java).stub {
            on { fileAccessStrategy }.thenReturn(cFileAccessStrategy)
        }
        val compilerContext = mock(CompilerContext::class.java).stub {
            on { config }.thenReturn(compilerConfig)
        }

        // Act
        val result = runBlocking { command.process(jsonObject, compilerContext) }

        // Assert
        assertNull(result)
    }

    @Test
    fun `process should return JSON element when FileAccessStrategy reads file successfully`() {
        // Arrange
        val command = IncludeCommand()
        val jsonPrimitive = JsonPrimitive("")
        val jsonObject = JsonObject().apply {
            add("file", JsonPrimitive("path/to/file.json"))
        }


        val cFileAccessStrategy = mock(FileAccessStrategy::class.java).stub {
            onBlocking { read("path/to/file.json") }.thenReturn(jsonPrimitive)
        }
        val compilerConfig = mock(CompilerConfig::class.java).stub {
            on { fileAccessStrategy }.thenReturn(cFileAccessStrategy)
        }
        val compilerContext = mock(CompilerContext::class.java).stub {
            on { config }.thenReturn(compilerConfig)
        }

        // Act
        val result = runBlocking { command.process(jsonObject, compilerContext) }

        // Assert
        assertNotNull(result)
        assertTrue(result is JsonElement)
    }

    @Test
    fun `canRunInContext should return true when FileAccessStrategy is available`() {
        // Arrange
        val command = IncludeCommand()
        val cc = mock(CompilerConfig::class.java).stub {
            on { fileAccessStrategy }.thenReturn(mock(FileAccessStrategy::class.java))
        }
        val compilerContext = mock(CompilerContext::class.java).stub {
            on { config }.thenReturn(cc)
        }

        // Act
        val result = command.canRunInContext(compilerContext)

        // Assert
        assertTrue(result)
    }

    @Test
    fun `canRunInContext should return false when FileAccessStrategy is not available`() {
        // Arrange
        val command = IncludeCommand()
        val compilerConfig = mock(CompilerConfig::class.java).stub {
            on { fileAccessStrategy }.thenReturn(null)
        }
        val compilerContext = mock(CompilerContext::class.java).stub {
            on { config }.thenReturn(compilerConfig)
        }

        // Act
        val result = command.canRunInContext(compilerContext)

        // Assert
        assertFalse(result)
    }
}

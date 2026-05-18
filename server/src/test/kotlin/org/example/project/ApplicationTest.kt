package org.example.project

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.example.project.model.AuthRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    /**
     * Best Practice: Use a helper for common setup to avoid repeating `application { module() }`
     * and to configure the client once (e.g., for JSON support).
     */
    private fun testApp(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            module()
        }
        block()
    }

    @Test
    fun `root url should return welcome message`() = testApp {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, Ktor!", bodyAsText())
        }
    }

    @Test
    fun `hello endpoint should handle query parameters`() = testApp {
        // Test with parameter
        client.get("/hello?name=Someone").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, Someone!", bodyAsText())
        }

        // Test with default value (no parameter)
        client.get("/hello").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, Anonymous!", bodyAsText())
        }
    }

    @Test
    fun `authenticate should receive and echo JSON credentials`() = testApp {
        val authData = AuthRequest(username = "Someone", password = "passw0rd")
        client.post("/authenticate") {
            contentType(ContentType.Application.Json)
            // Manual serialization to JSON string instead of using a JSON-configured client
            setBody(Json.encodeToString(authData))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Someone passw0rd is received", bodyAsText())
        }
    }

    @Test
    fun `delete endpoint should echo the username from path`() = testApp {
        val targetUser = "boyang"
        client.delete("/delete/$targetUser").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("$targetUser is deleted", bodyAsText())
        }
    }

    @Test
    fun `non-existent route should return 404`() = testApp {
        client.get("/does-not-exist").apply {
            assertEquals(HttpStatusCode.NotFound, status)
        }
    }
}

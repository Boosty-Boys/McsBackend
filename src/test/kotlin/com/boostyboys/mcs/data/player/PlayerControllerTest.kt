package com.boostyboys.mcs.data.player

import com.boostyboys.mcs.di.bindHttpClient
import com.boostyboys.mcs.di.bindSingleton
import com.boostyboys.mcs.kodeinApplication
import com.boostyboys.mcs.model.response.ErrorMessage
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class PlayerControllerTest {
    @Test
    fun `if team id not provided, error is thrown`() = testApplication {
        application {
            kodeinApplication {
                bindHttpClient()
                bindSingleton { di -> PlayerController(di) }
            }
        }

        val response = client.get("/players?date=4-20-2022")
        assertEquals(HttpStatusCode.OK, response.status)
        val deserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError.value, deserialized.httpStatusCode)
    }

    @Test
    fun `if date not provided, error is thrown`() = testApplication {
        application {
            kodeinApplication {
                bindHttpClient()
                bindSingleton { di -> PlayerController(di) }
            }
        }

        val response = client.get("/players?team_id=123")
        assertEquals(HttpStatusCode.OK, response.status)
        val deserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError.value, deserialized.httpStatusCode)
    }

    @Test
    fun `if no query parameters provided, error is thrown`() = testApplication {
        application {
            kodeinApplication {
                bindHttpClient()
                bindSingleton { di -> PlayerController(di) }
            }
        }

        val response = client.get("/players")
        assertEquals(HttpStatusCode.OK, response.status)
        val deserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError.value, deserialized.httpStatusCode)
    }
}

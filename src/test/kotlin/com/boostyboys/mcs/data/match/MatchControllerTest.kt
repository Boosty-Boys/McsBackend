package com.boostyboys.mcs.data.match

import com.boostyboys.mcs.di.bindHttpClient
import com.boostyboys.mcs.di.bindSingleton
import com.boostyboys.mcs.kodeinApplication
import com.boostyboys.mcs.model.response.ErrorMessage
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class MatchControllerTest {
    @Test
    fun `if season number not provided, error is thrown`() = testApplication {
        application {
            kodeinApplication {
                bindHttpClient()
                bindSingleton { di -> MatchController(di) }
            }
        }

        val response = client.get("/matches?leagueId=1")
        assertEquals(HttpStatusCode.OK, response.status)
        val deserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError.value, deserialized.httpStatusCode)
    }

    @Test
    fun `if league id not provided, error is thrown`() = testApplication {
        application {
            kodeinApplication {
                bindHttpClient()
                bindSingleton { di -> MatchController(di) }
            }
        }

        val response = client.get("/matches?seasonNumber=1")
        assertEquals(HttpStatusCode.OK, response.status)
        val deserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError.value, deserialized.httpStatusCode)
    }

    @Test
    fun `if no query parameters provided, error is thrown`() = testApplication {
        application {
            kodeinApplication {
                bindHttpClient()
                bindSingleton { di -> MatchController(di) }
            }
        }

        val response = client.get("/matches")
        assertEquals(HttpStatusCode.OK, response.status)
        val deserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError.value, deserialized.httpStatusCode)
    }
}

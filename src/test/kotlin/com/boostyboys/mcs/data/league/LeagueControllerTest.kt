package com.boostyboys.mcs.data.league

import com.boostyboys.mcs.di.bindHttpClient
import com.boostyboys.mcs.di.bindSingleton
import com.boostyboys.mcs.kodeinApplication
import com.boostyboys.mcs.model.remote.response.ErrorMessage
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class LeagueControllerTest {
    @Test
    fun `if season number not provided, error is thrown`() = testApplication {
        application {
            kodeinApplication {
                bindHttpClient()
                bindSingleton { di -> LeagueController(di) }
            }
        }

        val response = client.get("/leagues")
        assertEquals(HttpStatusCode.OK, response.status)
        val deserialized = Json.decodeFromString<ErrorMessage>(response.bodyAsText())
        assertEquals(HttpStatusCode.InternalServerError.value, deserialized.httpStatusCode)
    }
}

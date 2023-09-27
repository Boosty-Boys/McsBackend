package com.boostyboys.mcs.di

import com.boostyboys.mcs.model.DEFAULT_HEADERS
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.kodein.di.DI

private const val BASE_URL = "https://cfb2ensmt0.execute-api.us-east-1.amazonaws.com/prod/v2/"
private const val TIMEOUT_MS = 10_000L

@OptIn(ExperimentalSerializationApi::class)
fun DI.MainBuilder.bindHttpClient() {
    bindSingleton {
        HttpClient(CIO) {
            install(Logging)
            defaultRequest {
                url(BASE_URL)
                headers {
                    this.appendAll(DEFAULT_HEADERS)
                }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }
            engine {
                endpoint {
                    connectTimeout = TIMEOUT_MS
                }
            }
        }
    }
}

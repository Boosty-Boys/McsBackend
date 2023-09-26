package com.boostyboys.mcs.model

import io.ktor.util.StringValuesImpl

internal val DEFAULT_HEADERS = StringValuesImpl(
    values = mapOf(
        "x-api-key" to listOf(System.getenv("MCS_API_KEY")),
    ),
)

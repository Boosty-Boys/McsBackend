package com.boostyboys.mcs.model

import io.ktor.server.application.ApplicationCall

internal fun ApplicationCall.requireQueryParameter(
    parameter: String,
): String {
    val queryParameter = this.request.queryParameters[parameter]
    require(queryParameter != null)
    return queryParameter
}

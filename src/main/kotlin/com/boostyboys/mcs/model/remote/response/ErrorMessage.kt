package com.boostyboys.mcs.model.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val httpStatusCode: Int,
    val message: String,

)

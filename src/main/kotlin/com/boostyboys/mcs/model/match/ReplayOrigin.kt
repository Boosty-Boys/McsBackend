package com.boostyboys.mcs.model.match

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplayOrigin(
    @SerialName("key")
    val key: String,
    @SerialName("source")
    val source: String,
)

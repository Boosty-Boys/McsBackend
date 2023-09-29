package com.boostyboys.mcs.model.remote.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Url(
    @SerialName("_id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("url")
    val url: String,
)

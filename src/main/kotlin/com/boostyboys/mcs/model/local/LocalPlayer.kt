package com.boostyboys.mcs.model.local

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalPlayer(
    @SerialName("name") val name: String,
)

package com.almagribii.goalmate.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("provider") val provider: String,
    @SerialName("created_at") val createdAt: String? = null
)
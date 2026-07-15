package com.almagribii.goalmate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Achievement(
    val id: String? = null,
    val title: String,
    val description: String,
    val icon: String,
    val xp: Int
)

@Serializable
data class UserAchievement(
    val id: String? = null,
    val userId: String,
    val achievementId: String,
    val earnedAt: String? = null
)
package com.almagribii.goalmate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GoalCategory(
    val id: String,
    val name: String,
    val icon: String?,
    val color: String?
)
package com.almagribii.goalmate.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GoalUnit(
    val id: String,
    val name: String,
    val symbol: String
)
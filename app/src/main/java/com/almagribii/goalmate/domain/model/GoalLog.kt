package com.almagribii.goalmate.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalLog(
    val id: String? = null,
    
    @SerialName("goal_id")
    val goalId: String,
    
    @SerialName("user_id")
    val userId: String,
    
    val value: Double,
    
    @SerialName("created_at")
    val createdAt: String? = null,
    
    // UI Only fields (populated via join or mapping)
    @SerialName("goal_title")
    val goalTitle: String? = null
)
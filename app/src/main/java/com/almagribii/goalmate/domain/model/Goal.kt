package com.almagribii.goalmate.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Goal(
    val id: String? = null,

    @SerialName("user_id")
    val userId: String,

    @SerialName("category_id")
    val categoryId: String?,

    @SerialName("unit_id")
    val unitId: String?,

    val title: String,
    val description: String?,

    @SerialName("target_value")
    val targetValue: Double,

    @SerialName("current_value")
    val currentValue: Double = 0.0,

    val deadline: String?,
    val priority: String = "medium",
    val status: String = "active",

    @SerialName("repeat_type")
    val repeatType: String = "daily",

    @SerialName("reminder_enabled")
    val reminderEnabled: Boolean = false,

    @SerialName("reminder_time")
    val reminderTime: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null,

    @SerialName("deleted_at")
    val deletedAt: String? = null,

    // Properti relasional embedding (tidak perlu dianotasi karena sudah dialias di repo)
    val category: GoalCategory? = null,
    val unit: GoalUnit? = null
)
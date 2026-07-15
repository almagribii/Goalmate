package com.almagribii.goalmate.data.repository

import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.domain.model.GoalCategory
import com.almagribii.goalmate.domain.model.GoalLog
import com.almagribii.goalmate.domain.model.GoalUnit
import com.almagribii.goalmate.domain.repository.GoalRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : GoalRepository {

    override fun getActiveGoals(userId: String): Flow<List<Goal>> = flow {
        val response = postgrest.from("goals")
            .select(Columns.raw("*, category:goal_categories(*), unit:goal_units(*)")) {
                filter {
                    eq("user_id", userId)
                    eq("status", "active")
                    filter("deleted_at", FilterOperator.IS, "null")
                }
            }
            .decodeList<Goal>()
        emit(response)
    }

    override fun getHistoryGoals(userId: String): Flow<List<Goal>> = flow {
        val response = postgrest.from("goals")
            .select(Columns.raw("*, category:goal_categories(*), unit:goal_units(*)")) {
                filter {
                    eq("user_id", userId)
                    neq("status", "active")
                }
            }
            .decodeList<Goal>()
        emit(response)
    }

    override suspend fun insertGoal(goal: Goal): Result<Unit> = runCatching {
        postgrest.from("goals").insert(goal)
    }

    override suspend fun updateGoalProgress(goalId: String, newValue: Double, status: String): Result<Unit> = runCatching {
        postgrest.from("goals").update({
            set("current_value", newValue)
            set("status", status)
        }) {
            filter {
                eq("id", goalId)
            }
        }
    }

    override suspend fun getCategories(): Result<List<GoalCategory>> = runCatching {
        postgrest.from("goal_categories")
            .select()
            .decodeList<GoalCategory>()
    }

    override suspend fun getUnits(): Result<List<GoalUnit>> = runCatching {
        postgrest.from("goal_units")
            .select()
            .decodeList<GoalUnit>()
    }

    override fun getCompletedGoals(userId: String): Flow<List<Goal>> = flow {
        val response = postgrest.from("goals")
            .select(Columns.raw("*, category:goal_categories(*), unit:goal_units(*)")) {
                filter {
                    eq("user_id", userId)
                    eq("status", "completed")
                }
            }
            .decodeList<Goal>()
        emit(response)
    }

    override fun getRecentActivity(userId: String): Flow<List<GoalLog>> = flow {
        try {
            val response = postgrest.from("goal_logs")
                .select(Columns.raw("*, goal_title:goals(title)")) {
                    filter {
                        eq("user_id", userId)
                    }
                    limit(5)
                    order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                }
                .decodeList<JsonObject>()
                .map { json ->
                    val goalTitleObj = json["goal_title"]?.let {
                        if (it is JsonObject) it
                        else if (it is JsonArray && it.isNotEmpty()) it[0].jsonObject
                        else null
                    }
                    val goalTitle = goalTitleObj?.get("title")?.jsonPrimitive?.contentOrNull
                    
                    GoalLog(
                        id = json["id"]?.jsonPrimitive?.contentOrNull,
                        goalId = json["goal_id"]?.jsonPrimitive?.contentOrNull ?: "",
                        userId = json["user_id"]?.jsonPrimitive?.contentOrNull ?: "",
                        value = json["value"]?.jsonPrimitive?.doubleOrNull ?: 0.0,
                        createdAt = json["created_at"]?.jsonPrimitive?.contentOrNull,
                        goalTitle = goalTitle
                    )
                }
            emit(response)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override suspend fun deleteGoal(goalId: String): Result<Unit> = runCatching {
        postgrest.from("goals").update({
            set("deleted_at", "now()")
        }) {
            filter {
                eq("id", goalId)
            }
        }
    }

    override suspend fun updateGoal(goal: Goal): Result<Unit> = runCatching {
        postgrest.from("goals").update(goal) {
            filter {
                eq("id", goal.id ?: "")
            }
        }
    }
}
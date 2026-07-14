package com.almagribii.goalmate.data.repository

import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.domain.model.GoalCategory
import com.almagribii.goalmate.domain.model.GoalUnit
import com.almagribii.goalmate.domain.repository.GoalRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
}
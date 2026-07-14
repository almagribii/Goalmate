package com.almagribii.goalmate.domain.repository

import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.domain.model.GoalCategory
import com.almagribii.goalmate.domain.model.GoalUnit
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    // Mengambil target aktif milik user
    fun getActiveGoals(userId: String): Flow<List<Goal>>

    // Mengambil arsip/riwayat target
    fun getHistoryGoals(userId: String): Flow<List<Goal>>

    // Menambahkan target baru ke database
    suspend fun insertGoal(goal: Goal): Result<Unit>

    // Memperbarui progres target
    suspend fun updateGoalProgress(goalId: String, newValue: Double, status: String): Result<Unit>

    // Mengambil data master untuk pilihan di Form Add Goal
    suspend fun getCategories(): Result<List<GoalCategory>>
    suspend fun getUnits(): Result<List<GoalUnit>>
}
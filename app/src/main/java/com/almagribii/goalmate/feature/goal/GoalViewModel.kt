package com.almagribii.goalmate.feature.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.almagribii.goalmate.core.common.UiState
import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.domain.model.GoalCategory
import com.almagribii.goalmate.domain.model.GoalUnit
import com.almagribii.goalmate.domain.repository.AuthRepository
import com.almagribii.goalmate.domain.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val authRepository: AuthRepository,
    private val postgrest: Postgrest
) : ViewModel() {

    private val _activeGoalsState = MutableStateFlow<UiState<List<Goal>>>(UiState.Loading)
    val activeGoalsState: StateFlow<UiState<List<Goal>>> = _activeGoalsState.asStateFlow()

    private val _streakState = MutableStateFlow(0)
    val streakState: StateFlow<Int> = _streakState.asStateFlow()

    private val _streakIncreasedEvent = MutableSharedFlow<Int>()
    val streakIncreasedEvent = _streakIncreasedEvent.asSharedFlow()

    private val _categoriesState = MutableStateFlow<UiState<List<GoalCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<GoalCategory>>> = _categoriesState.asStateFlow()

    private val _unitsState = MutableStateFlow<UiState<List<GoalUnit>>>(UiState.Loading)
    val unitsState: StateFlow<UiState<List<GoalUnit>>> = _unitsState.asStateFlow()

    private val _addGoalResult = MutableStateFlow<Result<Unit>?>(null)
    val addGoalResult: StateFlow<Result<Unit>?> = _addGoalResult.asStateFlow()

    private val _completedGoalsState = MutableStateFlow<UiState<List<Goal>>>(UiState.Loading)
    val completedGoalsState: StateFlow<UiState<List<Goal>>> = _completedGoalsState.asStateFlow()

    init {
        fetchActiveGoals()
    }

    fun fetchActiveGoals() {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    goalRepository.getActiveGoals(user.id)
                        .catch { exception ->
                            _activeGoalsState.value = UiState.Error(exception.message ?: "Unknown Error")
                        }
                        .collect { goals ->
                            _activeGoalsState.value = UiState.Success(goals)
                        }
                } else {
                    _activeGoalsState.value = UiState.Error("User tidak terautentikasi")
                }
            }
        }
    }

    fun loadMasterData() {
        viewModelScope.launch {
            _categoriesState.value = UiState.Loading
            _unitsState.value = UiState.Loading

            goalRepository.getCategories().fold(
                onSuccess = { _categoriesState.value = UiState.Success(it) },
                onFailure = { _categoriesState.value = UiState.Error(it.message ?: "Gagal memuat kategori") }
            )

            goalRepository.getUnits().fold(
                onSuccess = { _unitsState.value = UiState.Success(it) },
                onFailure = { _unitsState.value = UiState.Error(it.message ?: "Gagal memuat unit") }
            )
        }
    }

    fun addNewGoal(
        title: String,
        description: String?,
        categoryId: String?,
        unitId: String?,
        targetValue: Double,
        deadline: String,
        priority: String,
        repeatType: String
    ) {
        viewModelScope.launch {
            authRepository.currentUser.first()?.let { user ->
                val newGoal = Goal(
                    userId = user.id,
                    title = title,
                    description = description,
                    categoryId = categoryId,
                    unitId = unitId,
                    targetValue = targetValue,
                    currentValue = 0.0,
                    deadline = deadline,
                    priority = priority,
                    status = "active",
                    repeatType = repeatType
                )

                val result = goalRepository.insertGoal(newGoal)
                _addGoalResult.value = result

                if (result.isSuccess) {
                    updateStreak(user.id)
                    fetchActiveGoals()
                }
            }
        }
    }

    fun resetAddGoalResult() {
        _addGoalResult.value = null
    }

    fun updateGoalProgress(goal: Goal, additionalValue: Double) {
        viewModelScope.launch {
            val newValue = goal.currentValue + additionalValue
            val newStatus = if (newValue >= goal.targetValue) "completed" else "active"

            authRepository.currentUser.first()?.let { user ->
                goal.id?.let { goalId ->
                    goalRepository.updateGoalProgress(goalId, newValue, newStatus).fold(
                        onSuccess = {
                            updateStreak(user.id)
                            fetchActiveGoals()
                        },
                        onFailure = { it.printStackTrace() }
                    )
                }
            }
        }
    }

    fun fetchCompletedGoals() {
        viewModelScope.launch {
            _completedGoalsState.value = UiState.Loading
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    goalRepository.getCompletedGoals(user.id)
                        .catch { exception ->
                            _completedGoalsState.value = UiState.Error(exception.message ?: "Gagal memuat riwayat")
                        }
                        .collect { goals ->
                            _completedGoalsState.value = UiState.Success(goals)
                        }
                } else {
                    _completedGoalsState.value = UiState.Error("User tidak terautentikasi")
                }
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            authRepository.currentUser.collect { authUser ->
                if (authUser != null) {
                    try {
                        val response = postgrest.from("users")
                            .select(Columns.ALL) {
                                filter { eq("id", authUser.id) }
                            }
                            .decodeSingleOrNull<JsonObject>()

                        if (response == null) {
                            val newUser = buildJsonObject {
                                put("id", authUser.id)
                                put("full_name", authUser.fullName)
                                put("email", authUser.email)
                                put("streak_count", 0)
                            }
                            try {
                                postgrest.from("users").insert(newUser)
                            } catch (e: Exception) {
                                android.util.Log.e("StreakDebug", "Gagal inisialisasi user: ${e.message}")
                            }
                            _streakState.value = 0
                        } else {
                            val streak = response["streak_count"]?.jsonPrimitive?.intOrNull ?: 0
                            _streakState.value = streak
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun updateStreak(userId: String) {
        viewModelScope.launch {
            try {
                val response = postgrest.from("users")
                    .select(Columns.ALL) {
                        filter { eq("id", userId) }
                    }
                    .decodeSingleOrNull<JsonObject>()

                val currentStreak = response?.get("streak_count")?.jsonPrimitive?.intOrNull ?: 0
                val lastActivityStr = response?.get("last_activity_at")?.jsonPrimitive?.contentOrNull

                val now = java.time.OffsetDateTime.now()
                var newStreak = currentStreak
                var isIncreased = false

                if (!lastActivityStr.isNullOrBlank()) {
                    val lastActivity = java.time.OffsetDateTime.parse(lastActivityStr)
                    val diff = java.time.Duration.between(lastActivity, now)
                    val minutesDiff = diff.toMinutes()

                    when {
                        minutesDiff >= 1 -> {
                            // Selama jedanya minimal 1 menit, streak bertambah
                            newStreak += 1
                            isIncreased = true
                        }
                        minutesDiff == 0L -> {
                            // Masih di menit yang sama, jangan tambah streak (tunggu menit depan)
                            if (newStreak == 0) {
                                newStreak = 1
                                isIncreased = true
                            }
                        }
                    }
                } else {
                    newStreak = 1
                    isIncreased = true
                }

                val data = buildJsonObject {
                    put("id", userId)
                    put("streak_count", newStreak)
                    put("last_activity_at", now.toString())
                    if (response == null) {
                        authRepository.currentUser.first()?.let {
                            put("full_name", it.fullName)
                            put("email", it.email)
                        }
                    }
                }
                
                try {
                    postgrest.from("users").upsert(data)
                    _streakState.value = newStreak
                    if (isIncreased) {
                        _streakIncreasedEvent.emit(newStreak)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("StreakDebug", "Gagal upsert streak ke DB: ${e.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

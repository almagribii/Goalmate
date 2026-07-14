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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalViewModel @Inject constructor(
    private val goalRepository: GoalRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _activeGoalsState = MutableStateFlow<UiState<List<Goal>>>(UiState.Loading)
    val activeGoalsState: StateFlow<UiState<List<Goal>>> = _activeGoalsState.asStateFlow()

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


    private val _categoriesState = MutableStateFlow<UiState<List<GoalCategory>>>(UiState.Loading)
    val categoriesState: StateFlow<UiState<List<GoalCategory>>> = _categoriesState.asStateFlow()

    private val _unitsState = MutableStateFlow<UiState<List<GoalUnit>>>(UiState.Loading)
    val unitsState: StateFlow<UiState<List<GoalUnit>>> = _unitsState.asStateFlow()

    private val _addGoalResult = MutableStateFlow<Result<Unit>?>(null)
    val addGoalResult: StateFlow<Result<Unit>?> = _addGoalResult.asStateFlow()

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

            goal.id?.let { goalId ->
                goalRepository.updateGoalProgress(goalId, newValue, newStatus).fold(
                    onSuccess = {
                        fetchActiveGoals()
                    },
                    onFailure = { exception ->
                    }
                )
            }
        }
    }
}
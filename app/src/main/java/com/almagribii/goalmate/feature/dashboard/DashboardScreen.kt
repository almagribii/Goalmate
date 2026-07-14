package com.almagribii.goalmate.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.almagribii.goalmate.core.navigation.NavigationItem
import com.almagribii.goalmate.feature.dashboard.components.AddGoalBottomSheet
import com.almagribii.goalmate.feature.dashboard.components.DashboardBottomBar
import com.almagribii.goalmate.feature.dashboard.components.DashboardHeader
import com.almagribii.goalmate.feature.dashboard.components.HomeScreen
import com.almagribii.goalmate.feature.dashboard.components.StreakRewardDialog
import com.almagribii.goalmate.feature.goal.GoalViewModel
import com.almagribii.goalmate.feature.goal.MyGoalScreen
import com.almagribii.goalmate.feature.history.HistoryScreen
import com.almagribii.goalmate.feature.profile.ProfileScreen

@Composable
fun DashboardScreen(
    fullName: String,
    email: String,
    onLogoutClick: () -> Unit,
    goalViewModel: GoalViewModel = hiltViewModel()
) {
    var currentTab by remember { mutableStateOf<NavigationItem>(NavigationItem.Dashboard) }
    var showAddGoalSheet by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var latestStreak by remember { mutableIntStateOf(0) }
    val streakCount by goalViewModel.streakState.collectAsState()

    LaunchedEffect(Unit) {
        goalViewModel.fetchUserProfile()
        goalViewModel.streakIncreasedEvent.collect { newStreak ->
            latestStreak = newStreak
            showStreakDialog = true
        }
    }

    Scaffold(
        topBar = {
            DashboardHeader(
                currentTab = currentTab,
                fullName = fullName,
                streakCount = streakCount
            )
        },
        bottomBar = {
            DashboardBottomBar(
                currentTab = currentTab,
                onTabSelected = { selectedTab -> currentTab = selectedTab },
                onAddGoalClick = { showAddGoalSheet = true }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            when (currentTab) {
                NavigationItem.Dashboard -> HomeScreen()
                NavigationItem.MyGoal -> MyGoalScreen(viewModel = goalViewModel)
                NavigationItem.History -> HistoryScreen(viewModel = goalViewModel)
                NavigationItem.Profile -> ProfileScreen(fullName = fullName, email = email, onLogoutClick = onLogoutClick, viewModel = goalViewModel)
            }
        }
    }
    if (showAddGoalSheet) {
        AddGoalBottomSheet(
            viewModel = goalViewModel,
            onDismiss = { showAddGoalSheet = false }
        )
    }

    if (showStreakDialog) {
        StreakRewardDialog(
            streakCount = latestStreak,
            onDismiss = { showStreakDialog = false }
        )
    }
}
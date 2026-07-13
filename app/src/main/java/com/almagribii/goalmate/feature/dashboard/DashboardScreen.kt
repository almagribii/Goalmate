package com.almagribii.goalmate.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.almagribii.goalmate.core.navigation.NavigationItem
import com.almagribii.goalmate.feature.dashboard.components.DashboardBottomBar
import com.almagribii.goalmate.feature.dashboard.components.DashboardHeader
import com.almagribii.goalmate.feature.dashboard.components.HomeScreen
import com.almagribii.goalmate.feature.goal.MyGoalScreen
import com.almagribii.goalmate.feature.history.HistoryScreen

@Composable
fun DashboardScreen(
    fullName: String,
    onLogoutClick: () -> Unit
) {
    var currentTab by remember { mutableStateOf<NavigationItem>(NavigationItem.Dashboard) }

    Scaffold(
        topBar = { DashboardHeader(currentTab = currentTab) },
        bottomBar = {
            DashboardBottomBar(
                currentTab = currentTab,
                onTabSelected = { selectedTab -> currentTab = selectedTab },
                onAddGoalClick = { /* Nanti kita handle lembar dialog tambah goal di sini */ }
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
                NavigationItem.MyGoal -> MyGoalScreen()
                NavigationItem.History -> HistoryScreen()
                NavigationItem.Profile -> ProfileScreen(fullName = fullName, onLogoutClick = onLogoutClick)
            }
        }
    }
}
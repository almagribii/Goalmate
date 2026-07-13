package com.almagribii.goalmate.core.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Dashboard : NavigationItem("dashboard_home", Icons.Default.GridView, "Home")
    object MyGoal : NavigationItem("my_goal", Icons.Default.TrackChanges, "Goals")
    object History : NavigationItem("history", Icons.Default.History, "History")
    object Profile : NavigationItem("profile", Icons.Default.Person, "Profile")
}
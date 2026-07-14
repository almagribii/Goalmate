package com.almagribii.goalmate.feature.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almagribii.goalmate.core.navigation.NavigationItem

@Composable
fun DashboardBottomBar(
    currentTab: NavigationItem,
    onTabSelected: (NavigationItem) -> Unit,
    onAddGoalClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Dock Background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            color = Color(0xFF0F172A), // Elegant Deep Slate
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Tabs
                InnerTabItem(
                    item = NavigationItem.Dashboard,
                    isSelected = currentTab == NavigationItem.Dashboard,
                    onClick = { onTabSelected(NavigationItem.Dashboard) }
                )
                InnerTabItem(
                    item = NavigationItem.MyGoal,
                    isSelected = currentTab == NavigationItem.MyGoal,
                    onClick = { onTabSelected(NavigationItem.MyGoal) }
                )

                // Large Gap for the Anomaly Button
                Spacer(modifier = Modifier.width(64.dp))

                // Right Tabs
                InnerTabItem(
                    item = NavigationItem.History,
                    isSelected = currentTab == NavigationItem.History,
                    onClick = { onTabSelected(NavigationItem.History) }
                )
                InnerTabItem(
                    item = NavigationItem.Profile,
                    isSelected = currentTab == NavigationItem.Profile,
                    onClick = { onTabSelected(NavigationItem.Profile) }
                )
            }
        }

        // The "Anomali" Middle Button - Elevated above the dock
        Box(
            modifier = Modifier
                .offset(y = (-36).dp) // Lifted halfway above the top edge
                .size(64.dp)
                .shadow(12.dp, CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFB923C), // Light Vibrant Orange
                            Color(0xFFF97316)  // Dark Vibrant Orange
                        )
                    ),
                    shape = CircleShape
                )
                .clickable { onAddGoalClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Goal",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun InnerTabItem(item: NavigationItem, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = if (isSelected) Color(0xFFF97316) else Color(0xFF94A3B8),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            color = if (isSelected) Color.White else Color(0xFF94A3B8),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

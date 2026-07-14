package com.almagribii.goalmate.feature.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almagribii.goalmate.core.common.UiState
import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.feature.goal.components.UpdateProgressDialog

@Composable
fun MyGoalScreen(
    viewModel: GoalViewModel
) {
    val activeGoalsState by viewModel.activeGoalsState.collectAsState()

    // State untuk memantau kartu target mana yang sedang dipilih untuk diupdate
    var selectedGoalForUpdate by remember { mutableStateOf<Goal?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        when (val state = activeGoalsState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFF97316)
                )
            }
            is UiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("⚠️", fontSize = 32.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = state.message, color = Color.Red, fontSize = 14.sp)
                }
            }
            is UiState.Success -> {
                val goals = state.data
                if (goals.isEmpty()) {
                    EmptyGoalsView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(goals) { goal ->
                            GoalCardPremium(
                                goal = goal,
                                onClick = { selectedGoalForUpdate = goal } // Triger klik kartu
                            )
                        }
                    }
                }
            }
        }
    }

    // Tampilkan Dialog Update jika ada goal yang dipilih
    selectedGoalForUpdate?.let { goal ->
        UpdateProgressDialog(
            goal = goal,
            onDismiss = { selectedGoalForUpdate = null },
            onConfirm = { additionalValue ->
                viewModel.updateGoalProgress(goal, additionalValue)
            }
        )
    }
}

@Composable
fun GoalCardPremium(
    goal: Goal,
    onClick: () -> Unit
) {
    val categoryColor = remember(goal.category?.color) {
        try {
            Color(android.graphics.Color.parseColor(goal.category?.color ?: "#94A3B8"))
        } catch (e: Exception) {
            Color(0xFF94A3B8)
        }
    }

    val progressFraction = if (goal.targetValue > 0) {
        (goal.currentValue / goal.targetValue).toFloat().coerceIn(0f, 1f)
    } else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }, // Menambahkan interaksi klik
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(categoryColor.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = goal.category?.name ?: "General",
                        color = categoryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = goal.priority.uppercase(),
                    color = when (goal.priority.lowercase()) {
                        "high" -> Color(0xFFEF4444)
                        "medium" -> Color(0xFFF59E0B)
                        else -> Color(0xFF10B981)
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = goal.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            if (!goal.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = goal.description, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(text = "Progress", fontSize = 11.sp, color = Color.Gray)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = if (goal.currentValue % 1 == 0.0) goal.currentValue.toInt().toString() else goal.currentValue.toString(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = " / ${if (goal.targetValue % 1 == 0.0) goal.targetValue.toInt().toString() else goal.targetValue.toString()} ${goal.unit?.symbol ?: ""}",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 1.dp, start = 2.dp)
                        )
                    }
                }

                Text(
                    text = "${(progressFraction * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = categoryColor,
                trackColor = Color(0xFFF1F5F9)
            )
        }
    }
}

@Composable
fun EmptyGoalsView() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🎯", fontSize = 48.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Belum Ada Target Aktif", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ketuk tombol + di bawah untuk mulai menyusun target tokomu.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}
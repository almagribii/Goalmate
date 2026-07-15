package com.almagribii.goalmate.feature.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almagribii.goalmate.core.common.UiState
import com.almagribii.goalmate.core.common.shimmerEffect
import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.feature.goal.GoalViewModel
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: GoalViewModel = hiltViewModel()
) {
    val completedGoalsState by viewModel.completedGoalsState.collectAsState()
    val activeGoalsState by viewModel.activeGoalsState.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    fun onRefresh() {
        isRefreshing = true
        viewModel.fetchCompletedGoals()
        viewModel.fetchActiveGoals()
        isRefreshing = false
    }

    // Menggabungkan target aktif dan selesai sebagai representasi log aktivitas riwayat
    val activityLogs = remember(activeGoalsState, completedGoalsState) {
        val active = (activeGoalsState as? UiState.Success)?.data ?: emptyList()
        val completed = (completedGoalsState as? UiState.Success)?.data ?: emptyList()
        completed + active
    }

    var selectedFilter by remember { mutableStateOf("Today") }
    val filters = listOf("Today", "Yesterday", "This Week", "Older")

    val filteredLogs = remember(activityLogs, selectedFilter) {
        val today = LocalDate.now()
        activityLogs.filter { goal ->
            val dateStr = goal.updatedAt ?: goal.createdAt ?: return@filter false
            val date = try {
                OffsetDateTime.parse(dateStr)
                    .atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDate()
            } catch (e: Exception) {
                return@filter false
            }
            
            when (selectedFilter) {
                "Today" -> date == today
                "Yesterday" -> date == today.minusDays(1)
                "This Week" -> {
                    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                    date >= startOfWeek && date <= today
                }
                "Older" -> {
                    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                    date < startOfWeek
                }
                else -> true
            }
        }.sortedByDescending { it.updatedAt ?: it.createdAt }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchCompletedGoals()
        viewModel.fetchActiveGoals()
    }

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFDFF))
        ) {
            // --- 1. PILIHAN FILTER ATAS (Pill Tabs) ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filters) { filterText ->
                    val isSelected = filterText == selectedFilter
                    FilterPill(
                        text = filterText,
                        isSelected = isSelected,
                        onClick = { selectedFilter = filterText }
                    )
                }
            }

            // --- 2. TIMELINE LIST VERTIKAL ---
            if ((completedGoalsState is UiState.Loading || activeGoalsState is UiState.Loading) && !isRefreshing) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(5) {
                        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            Box(modifier = Modifier.width(65.dp).fillMaxHeight(), contentAlignment = Alignment.TopCenter) {
                                Box(modifier = Modifier.padding(top = 14.dp).size(24.dp).clip(CircleShape).shimmerEffect())
                            }
                            Box(modifier = Modifier.weight(1f).height(100.dp).clip(RoundedCornerShape(20.dp)).shimmerEffect())
                        }
                    }
                }
            } else if (filteredLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada riwayat aktivitas untuk kategori ini kawan",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(filteredLogs) { goal ->
                        TimelineActivityRow(
                            goal = goal,
                            onDeleteClick = {
                                goal.id?.let { viewModel.deleteGoal(it) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF6366F1) else Color(0xFFF1F5F9),
            contentColor = if (isSelected) Color.White else Color(0xFF64748B)
        ),
        shape = RoundedCornerShape(99.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        modifier = Modifier.height(38.dp)
    ) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TimelineActivityRow(
    goal: Goal,
    onDeleteClick: () -> Unit
) {
    val isCompleted = goal.status.lowercase() == "completed"
    val progress = if (goal.targetValue > 0) (goal.currentValue / goal.targetValue).toFloat() else 0f
    
    val timeLabel = remember(goal.updatedAt, goal.createdAt) {
        val dateStr = goal.updatedAt ?: goal.createdAt ?: ""
        try {
            val dateTime = OffsetDateTime.parse(dateStr).atZoneSameInstant(ZoneId.systemDefault())
            val date = dateTime.toLocalDate()
            val today = LocalDate.now()
            
            when {
                date == today -> dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                date == today.minusDays(1) -> "Yest"
                else -> date.format(DateTimeFormatter.ofPattern("dd/MM"))
            }
        } catch (e: Exception) {
            "Time"
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) 
    ) {
        // --- BLOK TIMELINE KIRI (WAKTU & GARIS VERTIKAL) ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(65.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = timeLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Dot Indikator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF38BDF8).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = if (isCompleted) Color(0xFF059669) else Color(0xFF0284C7),
                    modifier = Modifier.size(14.dp)
                )
            }

            // Garis vertikal
            Box(
                modifier = Modifier
                    .weight(1f)
                    .width(1.5.dp)
                    .background(Color(0xFFE2E8F0))
            )
        }

        // --- BLOK KARTU RIWAYAT KANAN ---
        Card(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp, bottom = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isCompleted) Color(0xFFECFDF5).copy(alpha = 0.7f) else Color(0xFFEFF6FF).copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCompleted) "Selesai" else "Progres",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                    
                    Box {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Hapus Riwayat") },
                                onClick = {
                                    onDeleteClick()
                                    expanded = false
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = goal.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = if (isCompleted) Color(0xFF10B981) else Color(0xFF4F46E5),
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit?.symbol ?: ""}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isCompleted) Color(0xFF10B981) else Color(0xFF6366F1),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }
    }
}
package com.almagribii.goalmate.feature.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almagribii.goalmate.core.common.UiState
import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.domain.model.GoalCategory
import com.almagribii.goalmate.feature.dashboard.components.AddGoalBottomSheet
import com.almagribii.goalmate.feature.dashboard.components.EditGoalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGoalScreen(
    viewModel: GoalViewModel = hiltViewModel()
) {
    val activeGoalsState by viewModel.activeGoalsState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<String?>("All") }

    var editingGoal by remember { mutableStateOf<Goal?>(null) }
    var updatingProgressGoal by remember { mutableStateOf<Goal?>(null) }
    var showAddGoalSheet by remember { mutableStateOf(false) }

    val activeGoals = remember(activeGoalsState) {
        (activeGoalsState as? UiState.Success)?.data ?: emptyList()
    }

    val categories = remember(categoriesState) {
        (categoriesState as? UiState.Success)?.data ?: emptyList()
    }

    val filteredGoals = remember(activeGoals, searchQuery, selectedCategoryId) {
        activeGoals.filter { goal ->
            val matchesSearch = goal.title.contains(searchQuery, ignoreCase = true)
            val matchesCategory = if (selectedCategoryId == "All") {
                true
            } else {
                goal.categoryId == selectedCategoryId
            }
            matchesSearch && matchesCategory
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchActiveGoals()
        viewModel.loadMasterData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFDFF))
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search", color = Color(0xFF94A3B8), fontSize = 15.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            shape = RoundedCornerShape(99.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9).copy(alpha = 0.6f),
                unfocusedContainerColor = Color(0xFFF1F5F9).copy(alpha = 0.6f),
                disabledContainerColor = Color(0xFFF1F5F9).copy(alpha = 0.6f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            singleLine = true
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                GoalFilterPill(
                    text = "All",
                    isSelected = selectedCategoryId == "All",
                    onClick = { selectedCategoryId = "All" }
                )
            }
            items(categories) { category ->
                GoalFilterPill(
                    text = category.name,
                    isSelected = selectedCategoryId == category.id,
                    onClick = { selectedCategoryId = category.id }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredGoals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No active goals found",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAddGoalSheet = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Your First Goal")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredGoals) { goal ->
                    val matchedCategory = categories.find { it.id == goal.categoryId }
                    MyGoalCardItem(
                        goal = goal,
                        categoryName = matchedCategory?.name ?: "",
                        onItemClick = {
                            updatingProgressGoal = goal
                        },
                        onEditClick = {
                            editingGoal = goal
                        },
                        onDeleteClick = {
                            goal.id?.let { viewModel.deleteGoal(it) }
                        }
                    )
                }
            }
        }
    }

    if (editingGoal != null) {
        EditGoalBottomSheet(
            goal = editingGoal!!,
            viewModel = viewModel,
            onDismiss = { editingGoal = null }
        )
    }

    if (updatingProgressGoal != null) {
        UpdateProgressDialog(
            goal = updatingProgressGoal!!,
            onDismiss = { updatingProgressGoal = null },
            onConfirm = { additionalValue ->
                viewModel.updateGoalProgress(updatingProgressGoal!!, additionalValue)
                updatingProgressGoal = null
            }
        )
    }

    if (showAddGoalSheet) {
        AddGoalBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAddGoalSheet = false }
        )
    }
}

@Composable
fun UpdateProgressDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var progressText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Progres Target") },
        text = {
            Column {
                Text("Berapa banyak kemajuan yang kamu capai hari ini untuk '${goal.title}'?")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = progressText,
                    onValueChange = { progressText = it },
                    label = { Text("Nilai Progres") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = progressText.toDoubleOrNull()
                    if (value != null && value > 0) {
                        onConfirm(value)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun GoalFilterPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(if (isSelected) Color(0xFF6366F1) else Color(0xFFF1F5F9))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White else Color(0xFF475569)
        )
    }
}

@Composable
fun MyGoalCardItem(
    goal: Goal,
    categoryName: String?,
    onItemClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val progress = if (goal.targetValue > 0) (goal.currentValue / goal.targetValue).toFloat() else 0f
    var showMenu by remember { mutableStateOf(false) }

    val safeCategoryName = categoryName ?: ""
    val iconAndColor = remember(safeCategoryName) {
        when (safeCategoryName.lowercase()) {
            "education", "belajar", "kuliah" -> Icons.Default.School to Color(0xFF4FAAFF)
            "finance", "bisnis", "tabungan", "toko" -> Icons.Default.Work to Color(0xFF4CD964)
            "health", "sport", "olahraga", "run" -> Icons.Default.DirectionsRun to Color(0xFFFF6B9D)
            "creative", "art", "desain" -> Icons.Default.Palette to Color(0xFFFFB300)
            else -> Icons.Default.Assignment to Color(0xFF8B5CF6)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(iconAndColor.second.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconAndColor.first,
                            contentDescription = null,
                            tint = iconAndColor.second,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = goal.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEditClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onDeleteClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = iconAndColor.second,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Straighten,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${goal.currentValue.toInt()}/${goal.targetValue.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = goal.deadline?.ifBlank { "No Deadline" } ?: "No Deadline",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (goal.priority?.lowercase() == "high") Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (goal.priority?.lowercase() == "high") Color(0xFFFFB300) else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
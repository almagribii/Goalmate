package com.almagribii.goalmate.feature.dashboard.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almagribii.goalmate.core.common.UiState
import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.feature.goal.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGoalBottomSheet(
    goal: Goal,
    viewModel: GoalViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val categoriesState by viewModel.categoriesState.collectAsState()
    val unitsState by viewModel.unitsState.collectAsState()

    var title by remember { mutableStateOf(goal.title) }
    var description by remember { mutableStateOf(goal.description ?: "") }
    var targetValue by remember { mutableStateOf(goal.targetValue.toString()) }
    var deadline by remember { mutableStateOf(goal.deadline ?: "2026-12-31") }

    var selectedCategoryId by remember { mutableStateOf(goal.categoryId) }
    var selectedCategoryName by remember { 
        mutableStateOf(
            (categoriesState as? UiState.Success)?.data?.find { it.id == goal.categoryId }?.name ?: "Pilih Kategori"
        )
    }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var selectedUnitId by remember { mutableStateOf(goal.unitId) }
    var selectedUnitSymbol by remember { 
        mutableStateOf(
            (unitsState as? UiState.Success)?.data?.find { it.id == goal.unitId }?.symbol ?: "Pilih Unit"
        )
    }
    var unitDropdownExpanded by remember { mutableStateOf(false) }

    var selectedPriority by remember { mutableStateOf(goal.priority ?: "medium") }
    var selectedRepeat by remember { mutableStateOf(goal.repeatType ?: "daily") }

    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMasterData()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Edit Target Toko",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nama Target") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi (Opsional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = targetValue,
                    onValueChange = { targetValue = it },
                    label = { Text("Jumlah Target") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = selectedUnitSymbol,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Drop") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color(0xFF0F172A),
                            disabledBorderColor = Color.Gray,
                            disabledLabelColor = Color.Gray,
                            disabledTrailingIconColor = Color(0xFF0F172A)
                        )
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { unitDropdownExpanded = true })

                    DropdownMenu(
                        expanded = unitDropdownExpanded,
                        onDismissRequest = { unitDropdownExpanded = false }
                    ) {
                        when (val state = unitsState) {
                            is UiState.Success -> {
                                state.data.forEach { unit ->
                                    DropdownMenuItem(
                                        text = { Text("${unit.name} (${unit.symbol})") },
                                        onClick = {
                                            selectedUnitId = unit.id
                                            selectedUnitSymbol = unit.symbol
                                            unitDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategoryName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Kategori") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Drop") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color(0xFF0F172A),
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                        disabledTrailingIconColor = Color(0xFF0F172A)
                    )
                )
                Box(modifier = Modifier.matchParentSize().clickable { categoryDropdownExpanded = true })

                DropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.85f)
                ) {
                    when (val state = categoriesState) {
                        is UiState.Success -> {
                            state.data.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category.name) },
                                    onClick = {
                                        selectedCategoryId = category.id
                                        selectedCategoryName = category.name
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            Column {
                Text("Prioritas Target", fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("low", "medium", "high").forEach { priority ->
                        val isSelected = selectedPriority == priority
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                                .clickable { selectedPriority = priority }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = priority.uppercase(),
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Nama target tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val parsedValue = targetValue.toDoubleOrNull()
                    if (parsedValue == null || parsedValue <= 0) {
                        Toast.makeText(context, "Jumlah target harus diisi angka lebih dari 0!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isSubmitting = true
                    val updatedGoal = goal.copy(
                        title = title,
                        description = description.ifBlank { null },
                        categoryId = selectedCategoryId,
                        unitId = selectedUnitId,
                        targetValue = parsedValue,
                        deadline = deadline,
                        priority = selectedPriority,
                        repeatType = selectedRepeat
                    )
                    viewModel.updateGoal(updatedGoal)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                enabled = !isSubmitting,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Update Target", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

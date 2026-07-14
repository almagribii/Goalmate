package com.almagribii.goalmate.feature.goal.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almagribii.goalmate.domain.model.Goal

@Composable
fun UpdateProgressDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var inputValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White,
        title = {
            Text(
                text = "Update Progres",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Masukkan jumlah progres tambahan untuk target:\n\"${goal.title}\"",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    label = { Text("Jumlah Baru") },
                    placeholder = { Text("Misal: 5 atau 100000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = inputValue.toDoubleOrNull()
                    if (value != null && value > 0) {
                        onConfirm(value)
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
            ) {
                Text("Update", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}
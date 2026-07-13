package com.almagribii.goalmate.feature.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    onLogoutClick: () -> Unit
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Dashboard Goalmate",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tempat terbaik untuk mengelola target tokomu.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = onLogoutClick, // Menyambungkan aksi klik ke parameter
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout Icon"
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Sign Out dari Akun",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
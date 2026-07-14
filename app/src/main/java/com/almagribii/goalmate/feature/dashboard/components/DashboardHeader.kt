package com.almagribii.goalmate.feature.dashboard.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almagribii.goalmate.core.navigation.NavigationItem

@Composable
fun DashboardHeader(
    currentTab: NavigationItem,
    fullName: String
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("goalmate_prefs", Context.MODE_PRIVATE) }

    var headerImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(currentTab) {
        val savedBase64 = sharedPreferences.getString("profile_image_base64", null)
        if (savedBase64 != null) {
            val imageBytes = Base64.decode(savedBase64, Base64.DEFAULT)
            headerImageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } else {
            headerImageBitmap = null
        }
    }

    Surface(
        color = Color(0xFFF8FAFC),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = when (currentTab) {
                    NavigationItem.Dashboard -> "Home"
                    NavigationItem.MyGoal -> "My Goals"
                    NavigationItem.History -> "History"
                    NavigationItem.Profile -> "Profile"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1E293B)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { /* Handle buka halaman notifikasi */ },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(26.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (headerImageBitmap != null) {
                        Image(
                            bitmap = headerImageBitmap!!.asImageBitmap(),
                            contentDescription = "Header Profile Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFF97316)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fullName.take(1).uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
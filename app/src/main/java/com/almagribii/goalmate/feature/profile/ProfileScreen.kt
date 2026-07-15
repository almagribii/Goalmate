package com.almagribii.goalmate.feature.profile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almagribii.goalmate.core.common.UiState
import com.almagribii.goalmate.feature.goal.GoalViewModel

@Composable
fun ProfileScreen(
    fullName: String,
    email: String,
    onLogoutClick: () -> Unit,
    onNavigateToBadges: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("goalmate_prefs", Context.MODE_PRIVATE) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val streakCount by viewModel.streakState.collectAsState()

    val activeGoalsState by viewModel.activeGoalsState.collectAsState()
    val completedGoalsState by viewModel.completedGoalsState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.streakIncreasedEvent.collect { newStreak ->
            Toast.makeText(context, "🔥 Mantap! Streak kamu naik jadi $newStreak!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchActiveGoals()
        viewModel.fetchCompletedGoals()
        viewModel.fetchUserProfile()

        val savedBase64 = sharedPreferences.getString("profile_image_base64", null)
        if (savedBase64 != null) {
            val imageBytes = Base64.decode(savedBase64, Base64.DEFAULT)
            imageBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        }
    }

    val activeCount = remember(activeGoalsState) {
        (activeGoalsState as? UiState.Success)?.data?.size ?: 0
    }
    val completedCount = remember(completedGoalsState) {
        (completedGoalsState as? UiState.Success)?.data?.size ?: 0
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    imageBitmap = bitmap
                    val outputStream = java.io.ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                    val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
                    sharedPreferences.edit().putString("profile_image_base64", base64String).apply()
                    Toast.makeText(context, "Foto profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
                    .clickable {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Placeholder Icon",
                        modifier = Modifier.size(48.dp),
                        tint = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = fullName.ifBlank { "Brucad Al Magribi" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = email,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFE2E8F0))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            CircleStatItem(
                title = "Goals",
                value = activeCount.toString(),
                icon = Icons.Default.List,
                iconBgColor = Color(0xFFEFF6FF),
                iconTint = Color(0xFF2563EB)
            )
            CircleStatItem(
                title = "Completed",
                value = completedCount.toString(),
                icon = Icons.Default.Check,
                iconBgColor = Color(0xFFECFDF5),
                iconTint = Color(0xFF059669)
            )
            CircleStatItem(
                title = "Streak",
                value = streakCount.toString(), 
                icon = Icons.Default.Whatshot,
                iconBgColor = if (streakCount > 0) Color(0xFFFFF7ED) else Color(0xFFF1F5F9),
                iconTint = if (streakCount > 0) Color(0xFFF97316) else Color(0xFF94A3B8)
            )
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color(0xFFE2E8F0))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Settings",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            MenuRowItem(
                title = "My Badges",
                icon = Icons.Default.EmojiEvents,
                onClick = onNavigateToBadges
            )
            MenuRowItem(
                title = "Goals Created",
                icon = Icons.Default.List,
                onClick = {
                    Toast.makeText(context, "Anda memiliki $activeCount target aktif saat ini.", Toast.LENGTH_SHORT).show()
                }
            )
            MenuRowItem(
                title = email,
                icon = Icons.Default.Email,
                onClick = {
                    Toast.makeText(context, "Email Akun Terverifikasi.", Toast.LENGTH_SHORT).show()
                }
            )
            MenuRowItem(
                title = "Settings Menu",
                icon = Icons.Default.Settings,
                onClick = {
                    Toast.makeText(context, "Fitur Pengaturan Konfigurasi segera hadir kawan!", Toast.LENGTH_SHORT).show()
                }
            )
            MenuRowItem(
                title = "App Preferences",
                icon = Icons.Default.Settings,
                onClick = {
                    Toast.makeText(context, "Membuka Preferensi Tema & Tampilan.", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(8.dp))


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onLogoutClick()
                    }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout Icon",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Logout & Ganti Akun",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFEF4444)
                )
            }
        }
    }
}

@Composable
fun CircleStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = title, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
    }
}

@Composable
fun MenuRowItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() } // Memicu parameter Lambda click
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF0F172A),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0F172A)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow Go",
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(20.dp)
        )
    }
}
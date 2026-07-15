package com.almagribii.goalmate.feature.achievement

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almagribii.goalmate.domain.model.Achievement
import com.almagribii.goalmate.feature.goal.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    onBackClick: () -> Unit,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val allAchievements by viewModel.allAchievements.collectAsState()
    val earnedIds by viewModel.earnedAchievementIds.collectAsState()
    val xp by viewModel.totalXp.collectAsState()
    val level by viewModel.userLevel.collectAsState()
    val progress by viewModel.nextLevelXpProgress.collectAsState()

    val earnedBadges = remember(allAchievements, earnedIds) {
        allAchievements.filter { it.id in earnedIds }
    }
    val lockedBadges = remember(allAchievements, earnedIds) {
        allAchievements.filter { it.id !in earnedIds }
    }

    val topBadge = earnedBadges.firstOrNull() ?: allAchievements.firstOrNull()

    LaunchedEffect(Unit) {
        viewModel.fetchAchievementsData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Badges", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A))
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item(span = { GridItemSpan(3) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Level $level",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .width(180.dp)
                                .height(6.dp)
                                .clip(CircleShape),
                            color = Color(0xFF6366F1),
                            trackColor = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "XP $xp",
                            color = Color(0xFF94A3B8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(text = "🏆", fontSize = 80.sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = topBadge?.title ?: "Consistency Master",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = topBadge?.description ?: "Mulai kumpulkan lencana tokomu",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "Earned Badges",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
            }

            if (earnedBadges.isEmpty()) {
                item(span = { GridItemSpan(3) }) {
                    Text(
                        text = "Belum ada lencana yang diraih",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(earnedBadges) { badge ->
                    BadgeGridItem(badge = badge, isLocked = false)
                }
            }

            item(span = { GridItemSpan(3) }) {
                Text(
                    text = "Locked Badges",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                )
            }

            if (lockedBadges.isEmpty() && allAchievements.isNotEmpty()) {
                item(span = { GridItemSpan(3) }) {
                    Text(
                        text = "Semua lencana telah terbuka! 🎉",
                        color = Color(0xFF10B981),
                        fontSize = 13.sp
                    )
                }
            } else {
                items(lockedBadges) { badge ->
                    BadgeGridItem(badge = badge, isLocked = true)
                }
            }
        }
    }
}

@Composable
fun BadgeGridItem(badge: Achievement, isLocked: Boolean) {
    val iconVector = getAchievementIcon(badge.icon)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(if (isLocked) Color(0xFF1E293B) else Color(0xFFFFB300).copy(alpha = 0.15f))
                .border(
                    width = 2.dp,
                    color = if (isLocked) Color(0xFF334155) else Color(0xFFFFB300),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF475569),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = Color(0xFFFFB300),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = badge.title,
            color = if (isLocked) Color(0xFF64748B) else Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

fun getAchievementIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "workspace_premium" -> Icons.Default.WorkspacePremium
        "local_fire_department" -> Icons.Default.LocalFireDepartment
        "emoji_events" -> Icons.Default.EmojiEvents
        "military_tech" -> Icons.Default.MilitaryTech
        else -> Icons.Default.EmojiEvents
    }
}
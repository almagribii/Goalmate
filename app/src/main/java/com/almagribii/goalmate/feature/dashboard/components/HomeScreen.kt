package com.almagribii.goalmate.feature.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.almagribii.goalmate.core.common.UiState
import com.almagribii.goalmate.core.common.shimmerEffect
import com.almagribii.goalmate.domain.model.Goal
import com.almagribii.goalmate.domain.model.GoalCategory
import com.almagribii.goalmate.feature.goal.GoalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    fullName: String,
    viewModel: GoalViewModel = hiltViewModel()
) {
    val activeGoalsState by viewModel.activeGoalsState.collectAsState()
    val completedGoalsState by viewModel.completedGoalsState.collectAsState()
    val streakCount by viewModel.streakState.collectAsState()
    val categoriesState by viewModel.categoriesState.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }
    val pullToRefreshState = rememberPullToRefreshState()

    fun onRefresh() {
        isRefreshing = true
        viewModel.fetchActiveGoals()
        viewModel.fetchCompletedGoals()
        viewModel.fetchUserProfile()
        viewModel.loadMasterData()
        isRefreshing = false
    }

    val activeGoals = remember(activeGoalsState) {
        (activeGoalsState as? UiState.Success)?.data ?: emptyList()
    }
    val completedCount = remember(completedGoalsState) {
        (completedGoalsState as? UiState.Success)?.data?.size ?: 0
    }
    val categories = remember(categoriesState) {
        (categoriesState as? UiState.Success)?.data?.take(4) ?: emptyList()
    }

    val totalProgressPercent = remember(activeGoals, completedCount) {
        val total = activeGoals.size + completedCount
        if (total > 0) {
            ((completedCount.toFloat() / total.toFloat()) * 100).toInt()
        } else 0
    }

    LaunchedEffect(Unit) {
        viewModel.fetchActiveGoals()
        viewModel.fetchCompletedGoals()
        viewModel.fetchUserProfile()
        viewModel.loadMasterData()
    }

    PullToRefreshBox(
        state = pullToRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFBFDFF)),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 24.dp)
        ) {
            // --- 1. GOOD MORNING HEADER ---
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Good Morning,",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = fullName.split(" ").firstOrNull() ?: "Brucad",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "👋", fontSize = 22.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // --- 2. TODAY'S PROGRESS CARD ---
            item {
                if (activeGoalsState is UiState.Loading && !isRefreshing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .shimmerEffect()
                    )
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF6366F1), Color(0xFF38BDF8))
                                    )
                                )
                                .padding(20.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Today's Progress Card",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.size(70.dp)
                                    ) {
                                        val animatedProgress = totalProgressPercent.toFloat() / 100f
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            drawCircle(
                                                color = Color.White.copy(alpha = 0.2f),
                                                style = Stroke(width = 6.dp.toPx())
                                            )
                                            drawArc(
                                                color = Color.White,
                                                startAngle = -90f,
                                                sweepAngle = 360f * animatedProgress,
                                                useCenter = false,
                                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                        }
                                        Text(
                                            text = "$totalProgressPercent%",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$totalProgressPercent%",
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "Progress",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 12.sp
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "🔥 $streakCount",
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "Streak",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- 3. ROW CATEGORIES SHORTCUT (DINAMIS DARI SUPABASE) ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    if (categoriesState is UiState.Loading && !isRefreshing) {
                        repeat(4) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp, 80.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .shimmerEffect()
                            )
                        }
                    } else if (categories.isEmpty()) {
                        // Fallback seumpama data kategori belum termuat
                        CategoryShortcutItem("General", Icons.Default.Assignment, Color(0xFF64748B))
                    } else {
                        categories.forEach { category ->
                            val (icon, color) = getCategoryVisuals(category.name)
                            CategoryShortcutItem(category.name, icon, color)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // --- 4. UPCOMING GOALS SECTION ---
            item {
                Text(
                    text = "Upcoming Goals",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                if (activeGoalsState is UiState.Loading && !isRefreshing) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(3) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp, 130.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .shimmerEffect()
                            )
                        }
                    }
                } else if (activeGoals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Belum ada target mendatang kawan", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(activeGoals) { goal ->
                            // Kirim data list kategori untuk mencari nama kategori yang cocok di kartu target
                            val categoryList = (categoriesState as? UiState.Success)?.data ?: emptyList()
                            val matchedCategory = categoryList.find { it.id == goal.categoryId }
                            UpcomingGoalCard(goal = goal, categoryName = matchedCategory?.name ?: "")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }

            // --- 5. RECENT ACTIVITY SECTION ---
            item {
                Text(
                    text = "Recent Activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEFF6FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⚡", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Aktivitas Terkini Goalmate",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Kamu aktif mengupdate progres target barusan.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- FUNGSI PEMETAAN VISUAL KATEGORI UTAMA ---
fun getCategoryVisuals(categoryName: String): Pair<ImageVector, Color> {
    return when (categoryName.lowercase()) {
        "education", "belajar", "kuliah" -> Icons.Default.School to Color(0xFF4FAAFF)
        "finance", "bisnis", "tabungan", "toko" -> Icons.Default.Work to Color(0xFF4CD964)
        "health", "sport", "olahraga", "run" -> Icons.Default.DirectionsRun to Color(0xFFFF6B9D)
        "creative", "art", "desain" -> Icons.Default.Palette to Color(0xFFFFB300)
        else -> Icons.Default.Assignment to Color(0xFF8B5CF6)
    }
}

@Composable
fun CategoryShortcutItem(title: String, icon: ImageVector, bgColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bgColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = bgColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF64748B),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun UpcomingGoalCard(goal: Goal, categoryName: String) {
    val (catIcon, catColor) = getCategoryVisuals(categoryName)

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(catColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = catIcon,
                        contentDescription = null,
                        tint = catColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = goal.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${goal.currentValue.toInt()} terisi",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
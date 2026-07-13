package com.almagribii.goalmate.domain.repository

import com.almagribii.goalmate.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

//    membaca status session user saat ini
    val currentUser : Flow<User?>

//    fungsi untuk mroses ID token dari google signin ke supabase auth
    suspend fun signInWithGoogle(idToken: String): Result<Unit>

//    fungsi untuk logout dari sistem
    suspend fun signOut(): Result<Unit>
}
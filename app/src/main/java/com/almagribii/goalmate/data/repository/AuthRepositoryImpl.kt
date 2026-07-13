package com.almagribii.goalmate.data.repository

import com.almagribii.goalmate.data.model.User
import com.almagribii.goalmate.domain.repository.AuthRepository
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseAuth: Auth
) : AuthRepository {

    override val currentUser: Flow<User?> = supabaseAuth.sessionStatus.map { status ->
        val user = supabaseAuth.currentSessionOrNull()?.user
        if (user != null) {
            User(
                id = user.id,
                fullName = user.userMetadata?.get("full_name")?.toString()
                    ?: user.userMetadata?.get("name")?.toString()
                    ?: "Goalmate User",
                email = user.email ?: "",
                avatarUrl = user.userMetadata?.get("avatar_url")?.toString()
                    ?: user.userMetadata?.get("picture")?.toString(),
                provider = "google"
            )
        } else {
            null
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> {
        return runCatching {
            supabaseAuth.signInWith(IDToken) {
                this.idToken = idToken
                provider = Google
            }
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return runCatching {
            supabaseAuth.signOut()
        }
    }
}
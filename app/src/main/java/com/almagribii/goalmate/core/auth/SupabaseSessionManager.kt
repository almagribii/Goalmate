package com.almagribii.goalmate.core.auth

import android.content.Context
import io.github.jan.supabase.gotrue.SessionManager
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SupabaseSessionManager @Inject constructor(
    private val context: Context
) : SessionManager {

    private val prefs = context.getSharedPreferences("goalmate_auth_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun saveSession(session: UserSession) {
        val sessionJson = json.encodeToString(session)
        prefs.edit().putString("supabase_session", sessionJson).apply()
    }

    override suspend fun loadSession(): UserSession? {
        val sessionJson = prefs.getString("supabase_session", null)
        return if (sessionJson != null) {
            try {
                json.decodeFromString<UserSession>(sessionJson)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    override suspend fun deleteSession() {
        prefs.edit().remove("supabase_session").apply()
    }
}

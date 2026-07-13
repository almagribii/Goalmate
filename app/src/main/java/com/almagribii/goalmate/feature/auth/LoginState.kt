package com.almagribii.goalmate.feature.auth

data class LoginState (
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
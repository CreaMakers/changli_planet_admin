package com.dcelysia.changli_planet_admin.core.storage

import com.russhwolf.settings.Settings

object TokenManager {
    private const val ACCESS_TOKEN_KEY = "access_token"
    private const val USERNAME_KEY = "username"

    private val settings: Settings = Settings()

    fun saveToken(token: String) {
        settings.putString(ACCESS_TOKEN_KEY, token)
    }

    fun getToken(): String? {
        return settings.getStringOrNull(ACCESS_TOKEN_KEY)
    }

    fun saveUsername(username: String) {
        settings.putString(USERNAME_KEY, username)
    }

    fun getUsername(): String? {
        return settings.getStringOrNull(USERNAME_KEY)
    }

    fun clearAll() {
        settings.remove(ACCESS_TOKEN_KEY)
        settings.remove(USERNAME_KEY)
    }

    fun isLoggedIn(): Boolean {
        return getToken()?.isNotBlank() == true
    }
}
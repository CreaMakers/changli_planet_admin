package com.dcelysia.changli_planet_admin.feature.auth.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val code: String,
    val msg: String,
    val data: LoginData
)

@Serializable
data class LoginData(
    val access_token: String,
    val expires_in: String
)

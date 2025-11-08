package com.dcelysia.changli_planet_admin.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val code: String,
    val msg: String,
    val data: T
)


@Serializable
data class ApiNotDataResponse(
    val code: String,
    val msg: String,
)

package com.dcelysia.changli_planet_admin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
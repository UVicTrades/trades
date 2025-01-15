package ca.uvictrades.trades.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "uvictrades.db")
data class DatabaseProperties(
    val url: String,
    val username: String,
    val password: String
)
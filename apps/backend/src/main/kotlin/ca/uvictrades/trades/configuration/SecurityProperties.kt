package ca.uvictrades.trades.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "uvictrades.security")
data class SecurityProperties(
    val secret: String,
    val expirationSeconds: Int,
)
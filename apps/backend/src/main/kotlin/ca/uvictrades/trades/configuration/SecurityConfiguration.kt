package ca.uvictrades.trades.configuration

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class JwtGenerator(
    private val properties: SecurityProperties,
) {
    val key = Keys.hmacShaKeyFor(properties.secret.toByteArray())

    fun generate(withUsername: String): String =
        Jwts.builder()
            .subject(withUsername)
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(properties.expirationSeconds.toLong())))
            .signWith(key)
            .compact()
}

@Component
class JwtVerifier(
    private val properties: SecurityProperties,
) {
    val key = Keys.hmacShaKeyFor(properties.secret.toByteArray())

    fun verify(token: String): String =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
}

@Configuration
class SecurityConfiguration {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf {
                it.disable()
            }
            .formLogin {
                it.disable()
            }
            .httpBasic {
                it.disable()
            }
            .authorizeHttpRequests {
                it.anyRequest().permitAll()
            }
            .build()
    }

}
